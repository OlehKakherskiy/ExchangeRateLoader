package entity;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
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


    public Bank getBankFromName(String name) {
        for (Bank b : bankList)
            if (b.getName().compareTo(name) == 0)
                return b;
        return null;
    }
}
