package actions;

import annotation.Action;
import annotation.ContextAnnotation;
import annotation.Parameter;
import app.AbstractAction;
import entity.ExchangeRate;

import java.util.List;


@Action
@ContextAnnotation(list = {
        @Parameter(key = "exchangeRates", type = List.class)
})
public class CalculateDifferenceBetweenRates extends AbstractAction<ExchangeRate, Void> {


    @Override
    public ExchangeRate call() throws Exception {
        List<ExchangeRate> list = (List<ExchangeRate>) context.getValue("exchangeRates");
        ExchangeRate result = new ExchangeRate();
        if (list.size() < 2)
            return result;
        ExchangeRate first = list.get(0);
        ExchangeRate second;
        if(list.size() == 3 && list.get(0).getUpdateDate().isEqual(list.get(1).getUpdateDate()))
            second = list.get(2);
        else second = list.get(1);
        for (String key : first.getRate().keySet()) {
            Double sec = second.getRate().get(key);
            if (sec != null)
                result.getRate().put(key, first.getRate().get(key) - sec);
        }
        result.setUpdateDate(second.getUpdateDate());
        return result;
    }
}
