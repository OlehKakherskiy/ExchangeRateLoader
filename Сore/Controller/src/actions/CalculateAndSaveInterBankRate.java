package actions;

import annotation.Action;
import annotation.ContextAnnotation;
import annotation.Parameter;
import app.AbstractAction;
import entity.DateCount;
import entity.ExchangeRate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Action
@ContextAnnotation(list = {
        @Parameter(key = "banksRates", type = List.class),
        @Parameter(key = "storage"),
        @Parameter(key = "exchangeNames", type = List.class)
})
public class CalculateAndSaveInterBankRate extends AbstractAction<Map<String, Object>, Void> {

    @Override
    public Map<String, Object> call() throws Exception {
        ExchangeRate result = new ExchangeRate();
        List<ExchangeRate> banksRates = (List<ExchangeRate>) context.getValue("banksRates");
        List<String> exchangeNames = (List<String>) context.getValue("exchangeNames");
        for (String key : exchangeNames) {
            double resBuy = 0;
            double resSale = 0;
            int buyCount = 0;
            int saleCount = 0;
            for (ExchangeRate currentRate : banksRates) {
                Double buy = currentRate.getRate().get(key.concat("#buy"));
                if (buy != null) {
                    resBuy += buy.doubleValue();
                    buyCount++;
                }
                Double sale = currentRate.getRate().get(key.concat("#sale"));
                if (sale != null) {
                    resSale += sale.doubleValue();
                    saleCount++;
                }

            }
            result.getRate().put(key.concat("#buy"), resBuy / buyCount);
            result.getRate().put(key.concat("#sale"), resSale / saleCount);
        }
        String storage = (String) context.getValue("storage");
        List<ExchangeRate> lastTwoRates = ActionTemplates.readExchangeRates(storage, LocalDate.now(), exchangeNames, DateCount.PrevTwoDates);
        boolean reUpdate = (lastTwoRates.size() == 0 || !lastTwoRates.get(0).getUpdateDate().isEqual(LocalDate.now())) ? false : true;
        ActionTemplates.saveExchangeRate(storage, exchangeNames, result, reUpdate);
        if (reUpdate == true)
            lastTwoRates.set(0, result);
        else lastTwoRates.add(0, result);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("currentRate", result);
        resultMap.put("diffRate", ActionTemplates.calculateDifference(lastTwoRates));
        return resultMap;
    }
}
