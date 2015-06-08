package actions;

import annotation.Action;
import annotation.ContextAnnotation;
import annotation.Parameter;
import app.AbstractAction;
import app.AbstractView;
import app.Context;
import app.IActionBuilder;
import configuration.ConfigFacade;
import entity.*;
import javafx.collections.FXCollections;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;

@Action(updatesView = true)
@ContextAnnotation(list = {
        @Parameter(key = "requestView", type = AbstractView.class)
})
public class StartAction extends AbstractAction<Void, List<TableRowData>> {

    private IActionBuilder actionBuilder = ConfigFacade.getInstance().getActionBuilder();

    private static ExecutorService parallels = (ExecutorService) ConfigFacade.getInstance().getSystemProperty("parallels");

    @Override
    public Void call() throws Exception {
        BankList list = (BankList) ConfigFacade.getInstance().getSystemProperty("bankList");

        //TODO: оставить укрэксимбанк и сделать так, чтобы он возвращал историю
        List<TableRowData> tableData = FXCollections.observableArrayList();
        for (Bank b : list.getBankList()) {
            parallels.submit(() -> {
                try {
                    List<ExchangeRate> lastRate = readExchangeRates(b, LocalDate.now(), list);

                    System.out.println(lastRate.size());
                    ExchangeRate currentRate = null;
                    try {
                        currentRate = loadExchangeRate(b, list.getExchangeList());
                    } catch (Exception e) {
                    } //TODO:

                    TableRowData tableRowData = new TableRowData();
                    if (currentRate != null && currentRate.getRate().size() != 0) {
                        boolean reUpdate = (lastRate.get(0).getUpdateDate().isEqual(LocalDate.now())) ? true : false;
                        saveExchangeRate(b, list.getExchangeList(), currentRate, reUpdate);
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
        responseView.updateView(tableData);
        ((AbstractView) context.getValue("requestView")).setNextView(responseView);
        return null;
    }

    private List<ExchangeRate> readExchangeRates(Bank b, LocalDate date, BankList list) throws Exception {
        Context c = new Context();
        c.addValue("targetFile", b.getStorage());
        c.addValue("readBuyValue", true);
        c.addValue("readSaleValue", true);
        c.addValue("exchangeNames", list.getExchangeList());
        c.addValue("dateCount", DateCount.PrevTwoDates);
        c.addValue("startDate", date);
        AbstractAction<List<ExchangeRate>, Void> readAction = actionBuilder.buildActionObject("ReadExchangeRate", c);
        return readAction.call();
    }

    private ExchangeRate loadExchangeRate(Bank b, List<String> exchangeList) throws Exception {

        Context context = new Context((HashMap<String, Object>) b.getLoadContext().clone());
        context.addValue("exchangeList", exchangeList);
        context.addValue("bank", b);

        AbstractAction<ExchangeRate, Void> loader = actionBuilder.buildActionObject("URLLoader", context);
        return loader.call();
    }

    private void saveExchangeRate(Bank b, List<String> exchangeList, ExchangeRate rate, boolean reUpdate) {

        Context context = new Context();
        context.addValue("exchangeRate", rate);
        context.addValue("currencyList", exchangeList);
        context.addValue("storageFilePath", b.getStorage());
        context.addValue("reUpdate", reUpdate);

        AbstractAction action = actionBuilder.buildActionObject("SaveExchangeRate", context);
        action.setContext(context);
        try {
            action.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ExchangeRate calculateDifference(List<ExchangeRate> in) throws Exception {
        Context c = new Context();
        c.addValue("exchangeRates", in);

        AbstractAction<ExchangeRate, Void> calcDifferenceAction = actionBuilder.buildActionObject("CalculateDifferenceCommand", c);
        return calcDifferenceAction.call();
    }

    public static void main(String[] args) {
        StartAction start = new StartAction();
        try {
            start.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}