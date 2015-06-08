package actions;

import annotation.Action;
import annotation.ContextAnnotation;
import annotation.Parameter;
import app.AbstractAction;
import app.AbstractView;
import app.Context;
import configuration.ConfigFacade;
import entity.Bank;
import entity.BankList;
import entity.DateCount;
import entity.ExchangeRate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@Action(updatesView = true)
@ContextAnnotation(list = {
        @Parameter(key = "dateCount", type = DateCount.class),
        @Parameter(key = "readBuyValue", type = Boolean.class),
        @Parameter(key = "readSaleValue", type = Boolean.class),
        @Parameter(key = "banks", type = List.class),
        @Parameter(key = "requestView", type = AbstractView.class),
        @Parameter(key = "exchangeNames", type = List.class),
        @Parameter(key = "startDate", type = LocalDate.class)
})
public class ShowExchangeHistoryAction extends AbstractAction<Void, Map<String, Map<String, Map<LocalDate, Double>>>> {

    @Override
    public Void call() throws Exception {
        Map<String, List<ExchangeRate>> resultMap = new HashMap<>();
        List<Bank> banksData = (List<Bank>) context.getValue("banks");
        ExecutorService parallels = (ExecutorService) ConfigFacade.getInstance().getSystemProperty("parallels");
        for (Bank bank : banksData)
            parallels.submit(() -> {
                try {
                    resultMap.put(bank.getName(), getBanksExchangeRate(bank.getStorage()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        responseView.updateView(reformatData(resultMap));
        ((AbstractView) context.getValue("requestView")).setNextView(responseView);
        return null;
    }

    private Map<String, Map<String, Map<LocalDate, Double>>> reformatData(Map<String, List<ExchangeRate>> input) {
        Map<String, Map<String, Map<LocalDate, Double>>> resultMap = new HashMap<>();
        for (String key : input.keySet()) {
            Map<String, Map<LocalDate, Double>> exchangeRateGroupedByExchangeName = new HashMap<>();
            resultMap.put(key, exchangeRateGroupedByExchangeName);
            for (ExchangeRate rate : input.get(key)) {
                for (String exchangeNameAndOperation : rate.getRate().keySet()) {
                    if (exchangeRateGroupedByExchangeName.get(exchangeNameAndOperation) == null)
                        exchangeRateGroupedByExchangeName.put(exchangeNameAndOperation, new HashMap<>());
                    exchangeRateGroupedByExchangeName.get(exchangeNameAndOperation).put(rate.getUpdateDate(),
                            rate.getRate().get(exchangeNameAndOperation));
                }
            }
        }
        return resultMap;
    }


    private List<ExchangeRate> getBanksExchangeRate(String storage) throws Exception {
        Context c = new Context(context.getContext());
        c.addValue("targetFile", storage);
        AbstractAction<List<ExchangeRate>, Void> readAction = ConfigFacade.getInstance().getActionBuilder().buildActionObject("ReadExchangeRate", c);
        return readAction.call();
    }


    public static void main(String[] args) {
        Context c = new Context();
        c.addValue("dateCount", DateCount.Week);
        c.addValue("readBuyValue", true);
        c.addValue("readSaleValue", true);
        BankList l = (BankList) ConfigFacade.getInstance().getSystemProperty("bankList");
        Map<String, String> banksMap = new HashMap<>();
        for (Bank b : l.getBankList())
            banksMap.put(b.getName(), b.getStorage());
        c.addValue("banksNamesAndStorages", banksMap);
        c.addValue("exchangeNames", l.getExchangeList());
        c.addValue("startDate", LocalDate.now());
        ShowExchangeHistoryAction l1 = new ShowExchangeHistoryAction();
        l1.setContext(c);
        try {
            l1.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
