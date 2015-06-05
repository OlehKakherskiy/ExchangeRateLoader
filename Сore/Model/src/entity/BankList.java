package entity;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by User on 14.05.2015.
 */
@XmlRootElement(name = "banksConfigs")
@XmlAccessorType(XmlAccessType.FIELD)
public class BankList {

    @XmlElementWrapper(name = "currencyList")
    @XmlElement(name = "currency")
    private List<String> currencyList;

    @XmlElementWrapper(name = "bankList")
    @XmlElement(name = "bank")
    private List<Bank> bankList;

    public BankList() {
        currencyList = new ArrayList<>();
        bankList = new ArrayList<>();
    }

    public List<String> getExchangeList() {
        return currencyList;
    }

    public void setExchangeList(List<String> currencyList) {
        this.currencyList = currencyList;
    }

    public List<Bank> getBankList() {
        return bankList;
    }

    public void setBankList(List<Bank> bankList) {
        this.bankList = bankList;
    }

    public static void main(String[] args) {

        Bank b = new Bank();
        HashMap<String, Object> loadContext = new HashMap<>();
        loadContext.put("targetAttName", "class");
        loadContext.put("targetAttValue", "table table-striped");
        loadContext.put("targetTablePos", "-1");
        loadContext.put("rowParserID", "actions.DefaultRowParser");
        loadContext.put("salePos", 6+"");
        loadContext.put("buyPos", 5+"");
        loadContext.put("countPos", 3+"");
        loadContext.put("exchangeCodePos", 0 + "");
        b.setLoadContext(loadContext);
        b.setLoadContext(loadContext);
        b.setID("OshadBank");
        b.setURL("http://www.oschadbank.ua/ru/private/currency/currency_rates/");
        b.setName("Ощад Банк");
        b.setStorage("data/banksStorage/OshadBank.xml");
        BankList bankList = new BankList();
        ArrayList<String> list = new ArrayList<>();
        list.add("USD");
        list.add("EUR");
        list.add("RU");
        bankList.setExchangeList(list);
        bankList.getBankList().add(b);
        try {
            JAXBContext context = JAXBContext.newInstance(BankList.class, Bank.MapType.class);
//            Marshaller m = context.createMarshaller();
//            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
////            m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,"shema1.xsd");
//            m.marshal(bankList, new File("data/file.xml"));
            Unmarshaller um = context.createUnmarshaller();
            BankList blist = (BankList) um.unmarshal(new File("data/file.xml"));
            System.out.println();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
