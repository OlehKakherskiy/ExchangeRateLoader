package actions;

import annotation.Action;
import annotation.ContextAnnotation;
import annotation.Parameter;
import app.AbstractAction;
import entity.DateCount;
import entity.ExchangeRate;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 17.05.2015.
 */
@Action
@ContextAnnotation(list = {
        @Parameter(key = "dateCount", type = DateCount.class),
        @Parameter(key = "targetFile"),
        @Parameter(key = "startDate", type = LocalDate.class, optional = true),
        @Parameter(key = "exchangeNames", type = List.class),
        @Parameter(key = "readBuyValue", type = Boolean.class),
        @Parameter(key = "readSaleValue", type = Boolean.class)
})
public class ReadExchangeRateCommand extends AbstractAction<List<ExchangeRate>, Void> {


    private boolean readBuyValue;

    private boolean readSaleValue;

    private List<String> exchangeCodes;

    private ExchangeRate createAndFillExchangeRateObject(XMLStreamReader reader, LocalDate date) throws XMLStreamException {
        ExchangeRate result = new ExchangeRate();
        HashMap<String, Double> ExchangeRate = new HashMap<>();
        while (reader.hasNext()) {
            int code = reader.next();
            if (code == XMLStreamConstants.START_ELEMENT) {
                if (reader.getLocalName().compareTo("exchangeRate") == 0) {
                    result.setUpdateDate(LocalDate.parse(reader.getAttributeValue(null, "date")));
                    continue;
                }
                String currencyName = reader.getLocalName();
                if(!exchangeCodes.contains(currencyName))
                    continue;
                if (readBuyValue)
                    ExchangeRate.put(currencyName.concat("#buy"), Double.valueOf(reader.getAttributeValue(null, "buy").replace(',','.')));
                if(readSaleValue)
                    ExchangeRate.put(currencyName.concat("#sale"), Double.valueOf(reader.getAttributeValue(null, "sale").replace(',','.')));
            } else if (code == XMLStreamConstants.END_ELEMENT && reader.getLocalName().compareTo("exchangeRate") == 0) {
                break;
            }
        }
        result.setRate(ExchangeRate);
        if (date != null)
            result.setUpdateDate(date);
        return result;
    }

    private LocalDate calculateEndDate(LocalDate startDate) {
        DateCount dateCount = (DateCount) context.getValue("dateCount");
        switch (dateCount) {
            case PrevTwoDates:
                return startDate.minusDays(2);
            case Week:
                return startDate.minusDays(6);
            case Month:
                return startDate.minusMonths(1);
            case ThreeMonth:
                return startDate.minusMonths(3);
            case SixMonth:
                return startDate.minusMonths(6);
            case Year:
                return startDate.minusYears(1);
            case AllTime:
                return LocalDate.MIN;
        }
        return null;
    }

    private int compareDates(LocalDate start, LocalDate current, LocalDate end) {
        if (start.isBefore(current))
            return -1;
        if (end.isAfter(current))
            return +1;
        return 0;
    }


    @Override
    public List<ExchangeRate> call() throws Exception {
        readBuyValue = (boolean) context.getValue("readBuyValue");
        readSaleValue = (boolean) context.getValue("readSaleValue");
        exchangeCodes = (List<String>) context.getValue("exchangeNames");
        List<ExchangeRate> result = new ArrayList<>();
        LocalDate startDate = (LocalDate) context.getValue("startDate");
        LocalDate endDate = calculateEndDate(startDate);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        File f = new File((String) context.getValue("targetFile"));
        if (!f.exists()) {
            return result;
        }

        try {
            XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(f));
            while (reader.hasNext())
                if (reader.next() == XMLStreamConstants.START_ELEMENT && reader.getLocalName().compareTo("exchangeRate") == 0) {
                    LocalDate date = LocalDate.parse(reader.getAttributeValue(null, "date"));
                    int code = compareDates(startDate, date, endDate);
                    if (code == -1)
                        continue;
                    if (code == 1)
                        break;
                    result.add(createAndFillExchangeRateObject(reader, date));
                }
            reader.close();
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
}
