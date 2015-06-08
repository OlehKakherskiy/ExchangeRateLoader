package entity;

import javafx.collections.FXCollections;

import java.time.LocalDate;
import java.util.Map;

/**
 * Created by User on 17.05.2015.
 */
public class ExchangeRate {

    private Map<String, Double> rate;

    private LocalDate updateDate;

    public ExchangeRate() {
        rate = FXCollections.observableHashMap();
        updateDate = LocalDate.now();
    }

    public ExchangeRate(Map<String, Double> rate, LocalDate updateDate) {
        this.rate = FXCollections.observableMap(rate);
        this.updateDate = updateDate;
    }

    public Map<String, Double> getRate() {
        return rate;
    }

    public void setRate(Map<String, Double> rate) {
        this.rate.putAll(rate);
    }

    public LocalDate getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDate updateDate) {
        this.updateDate = updateDate;
    }
}
