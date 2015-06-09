package actions;

import annotation.Action;
import annotation.ContextAnnotation;
import annotation.Parameter;
import app.AbstractAction;
import app.AbstractView;
import app.Context;
import configuration.ConfigFacade;
import entity.Bank;
import entity.DateCount;
import entity.ExchangeRate;

import java.time.LocalDate;
import java.util.Collections;
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

    private volatile Map<String, List<ExchangeRate>> resultMap;

    @Override
    public Void call() throws Exception {
        resultMap = Collections.synchronizedMap(new HashMap<>());
        List<Bank> banksData = (List<Bank>) context.getValue("banks");
        ExecutorService parallels = (ExecutorService) ConfigFacade.getInstance().getSystemProperty("parallels");
        for (Bank bank : banksData) {
            if (bank != null)
                parallels.execute(() -> {
                    try {
                        resultMap.put(bank.getName(), getBanksExchangeRate(bank.getStorage()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
        }
        while (resultMap.size() < banksData.size())
            Thread.currentThread().sleep(3000);
        responseView.updateView(reformatData(resultMap));
        ((AbstractView) context.getValue("requestView")).setNextView(responseView);
        return null;
    }

    private Map<String, Map<String, Map<LocalDate, Double>>> reformatData(Map<String, List<ExchangeRate>> input) {
        Map<String, Map<String, Map<LocalDate, Double>>> resultMap = new HashMap<>(); //имя банка: карта[имя валюты: карта[дата-значение]]
        for (String key : input.keySet()) { //для каждого банка
            Map<String, Map<LocalDate, Double>> exchangeRateGroupedByExchangeName = new HashMap<>(); //карта группировки по валюте
            resultMap.put(key, exchangeRateGroupedByExchangeName);
            for (ExchangeRate rate : input.get(key)) { //список стоимостей валюты
                for (String exchangeNameAndOperation : rate.getRate().keySet()) { // для каждой валюты
                    if (exchangeRateGroupedByExchangeName.get(exchangeNameAndOperation) == null)
                        exchangeRateGroupedByExchangeName.put(exchangeNameAndOperation, new HashMap<>());
                    exchangeRateGroupedByExchangeName.get(exchangeNameAndOperation).put(rate.getUpdateDate(),
                            rate.getRate().get(exchangeNameAndOperation));
                }
            }
        }
        return resultMap;
    }


    private synchronized List<ExchangeRate> getBanksExchangeRate(String storage) throws Exception {
        Context c = new Context(context.getContext());
        c.addValue("targetFile", storage);
        AbstractAction<List<ExchangeRate>, Void> readAction = ConfigFacade.getInstance().getActionBuilder().buildActionObject("ReadExchangeRate", c);
        List<ExchangeRate> rate = readAction.call();
        return rate;
    }

}
