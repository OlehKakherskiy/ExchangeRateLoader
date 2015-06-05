package actions;

import annotation.Action;
import app.AbstractAction;
import app.Context;
import app.IActionBuilder;
import configuration.ConfigFacade;
import entity.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Action(needContext = false, updatesView = true)
public class StartAction extends AbstractAction {

    private IActionBuilder actionBuilder = ConfigFacade.getInstance().getActionBuilder();

    @Override
    public void run() {
        BankList list = (BankList) ConfigFacade.getInstance().getSystemProperty("bankList");
        for (Bank b : list.getBankList()) {
            try {
                TableRowData tableRowData = new TableRowData();

                List<ExchangeRate> lastRate = readExchangeRates(b);
                ExchangeRate currentRate = loadExchangeRate(b, list.getExchangeList());
                lastRate.add(currentRate);
                ExchangeRate differenceRate = calculateDifference(lastRate);

                tableRowData.setBank(b);
                tableRowData.setCurrentRate(currentRate);
                tableRowData.setRateDifference(differenceRate);

                boolean reUpdate = (!lastRate.isEmpty() && lastRate.get(0).getUpdateDate().isEqual(LocalDate.now())) ? true : false;
                saveExchangeRate(b, list.getExchangeList(), currentRate, reUpdate);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private List<ExchangeRate> readExchangeRates(Bank b) throws Exception {
        Context c = new Context();
        c.addValue("targetFile", b.getStorage());
        c.addValue("dateCount", DateCount.LastUpdate);
        AbstractAction<List<ExchangeRate>> readAction = actionBuilder.buildActionObject("ReadExchangeRate", c);
        return readAction.call();
    }

    private ExchangeRate loadExchangeRate(Bank b, List<String> exchangeList) throws Exception {

        Context context = new Context((HashMap<String, Object>) b.getLoadContext().clone());
        context.addValue("exchangeList", exchangeList);
        context.addValue("bank", b);

        AbstractAction<ExchangeRate> loader = actionBuilder.buildActionObject("URLLoader", context);
//        loader.setContext(context);
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
        action.run();
    }

    private ExchangeRate calculateDifference(List<ExchangeRate> in) throws Exception {
        Context c = new Context();
        c.addValue("exchangeRates", in);

        AbstractAction<ExchangeRate> calcDifferenceAction = actionBuilder.buildActionObject("CalculateDifferenceCommand", c);
        return calcDifferenceAction.call();
    }

    public static void main(String[] args) {
        StartAction start = new StartAction();
        start.run();
    }
}