package actions;

import annotation.Action;
import annotation.ContextAnnotation;
import annotation.Parameter;
import app.Context;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Action
@ContextAnnotation(list = {
        @Parameter(key = "buyPos", type = Integer.class, optional = true),
        @Parameter(key = "salePos", type = Integer.class, optional = true),
        @Parameter(key = "countPos", type = Integer.class, optional = true),
        @Parameter(key = "exchangeCodePos", type = Integer.class, optional = true),
        @Parameter(key = "exchangeList", type = List.class),
        @Parameter(key = "table", type = Element.class)
})
public class DefaultRowParser extends AbstractRowParser {

    private int buyPos;

    private int salePos;

    private int countPos;

    private int exchangeCodePos;


    public DefaultRowParser() {
        countPos = -1;
        salePos = -1;
        exchangeCodePos = -1;
        buyPos = -1;
    }

    @Override
    protected Map<String, Double> parseTable() {
        Element table = (Element) context.getValue("table");
        if (buyPos == -1) {//конфигурационные данные по таблицам с курсами валют на сайте отсутствуют - парсим шапку сайта
            parseHead(table.child(0));
        }

        HashMap<String, Double> exchangeRates = new HashMap<>();
        List<Element> children = table.children();
        for (Element row : children) {
            if (!row.text().trim().isEmpty())
                parseRow(row, exchangeRates);
        }
        return exchangeRates;
    }


    private void parseRow(Element el, Map<String, Double> exchangeRates) {
        List<Element> children = el.children();
        String exchangeElement = children.get(exchangeCodePos).text();
        for (String exchange : (List<String>) context.getValue("exchangeList")) {
            if (exchangeElement.contains(exchange)) {
                double buy = Double.valueOf(children.get(buyPos).ownText().replace(',','.'));
                double sale = Double.valueOf(children.get(salePos).ownText().replace(',', '.'));
                if (countPos > -1) {
                    buy /= Double.valueOf(children.get(countPos).ownText().replace(',', '.'));
                    sale /= Double.valueOf(children.get(countPos).ownText().replace(',','.'));
                }
                exchangeRates.put(new StringBuilder(exchange).append("#buy").toString(), buy);
                exchangeRates.put(new StringBuilder(exchange).append("#sale").toString(), sale);
            }
        }
    }

    private void parseHead(Element tableHead) {
        List<Element> elements = tableHead.children();
        int iter = 0;
        String elementText;
        for (Element e : tableHead.children()) {
            elementText = e.text().toLowerCase();
            if (elementText.contains("прод"))
                salePos = iter;
            else if (elementText.contains("куп") || elementText.contains("пок"))
                buyPos = iter;
            else if (elementText.contains("ном") || elementText.contains("количество") || elements.contains("кількість"))
                countPos = iter;
            else if ((elementText.contains("код") || elementText.contains("вал")) && exchangeCodePos < 0)
                exchangeCodePos = iter;
            iter++;
        }
    }

    @Override
    public void setContext(Context c) {
        if (c.getValue("buyPos") != null) {
            buyPos = ((Integer) c.getValue("buyPos")).intValue();
            salePos = ((Integer) c.getValue("salePos")).intValue();
            countPos = ((Integer) c.getValue("countPos")).intValue();
            exchangeCodePos = ((Integer) c.getValue("exchangeCodePos")).intValue();
        }
        this.context = c;
    }
}
