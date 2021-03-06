package actions;

import annotation.Action;
import annotation.ContextAnnotation;
import annotation.Parameter;
import app.AbstractAction;
import app.Context;
import configuration.ConfigFacade;
import entity.Bank;
import entity.ExchangeRate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Action
@ContextAnnotation(list = {
        @Parameter(key = "bank", type = Bank.class),
        @Parameter(key = "exchangeList", type = List.class)
})
public class UrlRateLoader extends AbstractAction {

    @Override
    public ExchangeRate call() throws Exception {
        try {
            Bank b = (Bank) context.getValue("bank");

            Document doc = Jsoup.connect(b.getURL()).userAgent("Mozilla").get();
            List<Element> l = doc.getElementsByAttributeValue((String) b.getLoadContext().get("targetAttName"), (String) b.getLoadContext().get("targetAttValue"));
            int targetTablePos = (Integer) b.getLoadContext().get("targetTablePos");
            Element targetElement = (targetTablePos == -1) ? l.get(0).child(0) : l.get(0).child(targetTablePos);
            Context c = new Context(b.getLoadContext());
            c.addValue("table", targetElement);
            c.addValue("exchangeList", context.getValue("exchangeList"));
            AbstractAction<Map<String, Double>, Void> defaultRowParser = ConfigFacade.getInstance().getActionBuilder().
                    buildActionObject((String) b.getLoadContext().get("rowParserID"), c);
            ExchangeRate rate = new ExchangeRate(defaultRowParser.call(), LocalDate.now());
            return rate;

        } catch (IOException e) {
            return new ExchangeRate(); //TODO:
        }
    }
}

