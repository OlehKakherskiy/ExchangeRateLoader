package actions;

import annotation.Action;
import annotation.ContextAnnotation;
import annotation.Parameter;
import app.AbstractAction;
import app.AbstractView;
import app.Context;
import configuration.ConfigFacade;
import entity.*;
import javafx.collections.FXCollections;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static actions.ActionTemplates.*;

@Action(updatesView = true)
@ContextAnnotation(list = {
        @Parameter(key = "requestView", type = AbstractView.class)
})
public class StartAction extends AbstractAction<Void, Map<String, Object>> {

    private static ExecutorService parallels = (ExecutorService) ConfigFacade.getInstance().getSystemProperty("parallels");

    private volatile List<TableRowData> tableData;

    @Override
    public Void call() throws Exception {
        BankList list = (BankList) ConfigFacade.getInstance().getSystemProperty("bankList");
        tableData = Collections.synchronizedList(FXCollections.observableArrayList());
        for (Bank b : list.getBankList()) {
            parallels.submit(() -> {
                try {
                    List<ExchangeRate> lastRate = readExchangeRates(b.getStorage(), LocalDate.now(), list.getExchangeList(), DateCount.PrevTwoDates);
                    ExchangeRate currentRate = null;
                    try {
                        currentRate = loadExchangeRate(b, list.getExchangeList());
                    } catch (Exception e) {}

                    TableRowData tableRowData = new TableRowData();
                    if (currentRate != null && currentRate.getRate().size() != 0) {
                        boolean reUpdate = (lastRate.get(0).getUpdateDate().isEqual(LocalDate.now())) ? true : false;
                        saveExchangeRate(b.getStorage(), list.getExchangeList(), currentRate, reUpdate);
                        if (reUpdate)
                            lastRate.set(0, currentRate);
                        else
                            lastRate.add(0, currentRate);
                    } else currentRate = lastRate.get(0);
                    ExchangeRate differenceRate = calculateDifference(lastRate);
                    tableRowData.setRateDifference(differenceRate);
                    tableRowData.setBank(b);
                    tableRowData.setCurrentRate(currentRate);
                    tableData.add(tableRowData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        while (tableData.size() < list.getBankList().size())
            Thread.currentThread().sleep(2000);
        Map<String, Object> result = new HashMap<>();
        result.put("tableRowData", tableData);
        result.putAll(getInterBankData());
        responseView.updateView(result);
        ((AbstractView) context.getValue("requestView")).setNextView(responseView);
        return null;
    }

    public Map<String, Object> getInterBankData() throws Exception {
        Context c = new Context();
        c.addValue("exchangeNames", ((BankList) ConfigFacade.getInstance().getSystemProperty("bankList")).getExchangeList());
        c.addValue("storage", ConfigFacade.getInstance().getSystemProperty("interBankDataStorage"));
        c.addValue("banksRates", tableData.stream().map(TableRowData::getCurrentRate).collect(Collectors.toList()));
        AbstractAction<Map<String, Object>, Void> action = ConfigFacade.getInstance().getActionBuilder().buildActionObject("getInterBankData", c);
        return action.call();
    }
}