package actions;

import app.AbstractAction;
import app.Context;
import app.IActionBuilder;
import configuration.ConfigFacade;
import entity.Bank;
import entity.DateCount;
import entity.ExchangeRate;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 09.06.2015.
 */
public class ActionTemplates {
    private static IActionBuilder actionBuilder = ConfigFacade.getInstance().getActionBuilder();

    public static List<ExchangeRate> readExchangeRates(String storage, LocalDate date, List<String> list, DateCount count) throws Exception {
        Context c = new Context();
        c.addValue("targetFile", storage);
        c.addValue("readBuyValue", true);
        c.addValue("readSaleValue", true);
        c.addValue("exchangeNames", list);
        c.addValue("dateCount", count);
        c.addValue("startDate", date);
        AbstractAction<List<ExchangeRate>, Void> readAction = actionBuilder.buildActionObject("ReadExchangeRate", c);
        return readAction.call();
    }

    public static ExchangeRate loadExchangeRate(Bank b, List<String> exchangeList) throws Exception {

        Context context = new Context((HashMap<String, Object>) b.getLoadContext().clone());
        context.addValue("exchangeList", exchangeList);
        context.addValue("bank", b);

        AbstractAction<ExchangeRate, Void> loader = actionBuilder.buildActionObject("URLLoader", context);
        return loader.call();
    }

    public static void saveExchangeRate(String storage, List<String> exchangeList, ExchangeRate rate, boolean reUpdate) {

        Context context = new Context();
        context.addValue("exchangeRate", rate);
        context.addValue("currencyList", exchangeList);
        context.addValue("storageFilePath", storage);
        context.addValue("reUpdate", reUpdate);

        AbstractAction action = actionBuilder.buildActionObject("SaveExchangeRate", context);
        action.setContext(context);
        try {
            action.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ExchangeRate calculateDifference(List<ExchangeRate> in) throws Exception {
        Context c = new Context();
        c.addValue("exchangeRates", in);

        AbstractAction<ExchangeRate, Void> calcDifferenceAction = actionBuilder.buildActionObject("CalculateDifferenceCommand", c);
        return calcDifferenceAction.call();
    }
}
