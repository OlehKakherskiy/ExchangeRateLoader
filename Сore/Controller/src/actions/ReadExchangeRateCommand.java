package actions;

import annotation.Action;
import annotation.ContextAnnotation;
import annotation.Parameter;
import app.AbstractAction;
import app.Context;
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
        @Parameter(key = "startDate", type = LocalDate.class, optional = true)
})
public class ReadExchangeRateCommand extends AbstractAction {

    private boolean lastUpdate = false;


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
                ExchangeRate.put(currencyName.concat("#").concat(reader.getAttributeLocalName(0)),
                        Double.valueOf(reader.getAttributeValue(0)));
                ExchangeRate.put(currencyName.concat("#").concat(reader.getAttributeLocalName(1)),
                        Double.valueOf(reader.getAttributeValue(1)));
            } else if (code == XMLStreamConstants.END_ELEMENT && reader.getLocalName().compareTo("ExchangeRate") == 0)
                break;
        }
        result.setRate(ExchangeRate);
        if (date != null)
            result.setUpdateDate(date);
        return result;
    }

    private LocalDate calculateEndDate(LocalDate startDate) {
        DateCount dateCount = (DateCount) context.getValue("dateCount");
        switch (dateCount) {
            case LastUpdate: {
                lastUpdate = true;
                return null;
            }
            case Week: {
                return startDate.minusDays(6);
            }
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
        List result = (List) new ArrayList<>();
        LocalDate startDate = (LocalDate) context.getValue("startDate");
        LocalDate endDate = calculateEndDate(startDate);
        XMLInputFactory factory = XMLInputFactory.newInstance();
        File f = new File((String) context.getValue("targetFile"));
        if (!f.exists())
            return result;

        try {
            XMLStreamReader reader = factory.createXMLStreamReader(new FileInputStream(f));
            if (lastUpdate) {
                reader.next();
                reader.next();
                reader.next();
                result.add(createAndFillExchangeRateObject(reader, null));
                reader.close();
                return result;
            }
            while (reader.hasNext())
                if (reader.next() == XMLStreamConstants.START_ELEMENT && reader.getLocalName().compareTo("ExchangeRate") == 0) {
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


    public static void main(String[] args) throws Exception {
        Context c = new Context();
        c.addValue("dateCount", DateCount.LastUpdate);
        c.addValue("targetFile", "data/banksStorage/PrivatBankCurrencyHistory.xml");
        c.addValue("startDate", LocalDate.now());
        ReadExchangeRateCommand r = new ReadExchangeRateCommand();
        r.context = c;
        List<ExchangeRate> rates = r.call();
        for (ExchangeRate rate : rates)
            System.out.println(rate.getUpdateDate() + " " + rate.getRate());
    }

}
