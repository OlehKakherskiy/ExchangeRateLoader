package viewController;

import configuration.ConfigFacade;
import entity.ExchangeRate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.Map;

/**
 * Created by User on 08.06.2015.
 */
public class InterBankViewController {

    private static ObservableList<String> exchangeNames = FXCollections.observableArrayList();

    static {
        Map<String, Object> exchanges = (Map<String, Object>) ConfigFacade.getInstance().getSystemProperty("exchangesToShow");
        for (String key : exchanges.keySet())
            exchangeNames.add((String) exchanges.get(key));
    }

    @FXML
    private ComboBox<String> exchangeRates;

    @FXML
    private Label exchangeRateValue;

    @FXML
    private Label rateChanging;

    @FXML
    private void initialize() {
        exchangeRates.getItems().clear();
        exchangeRates.getItems().addAll(exchangeNames);
        exchangeRates.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (currentRate == null || diffRate == null)
                return;
            rateChanging.setText(getDiffRate(newValue));
            exchangeRateValue.setText(getRate(newValue));
        });
    }

    private ExchangeRate currentRate;

    private ExchangeRate diffRate;

    private String getRate(String exchangeName) {
        return String.format("%.3f/%.3f", currentRate.getRate().get(exchangeName + "#buy"),
                currentRate.getRate().get(exchangeName + "#sale"));
    }

    private String getDiffRate(String exchangeName) {
        return String.format("%.3f/%.3f %s", diffRate.getRate().get(exchangeName + "#buy"),
                diffRate.getRate().get(exchangeName + "#sale"), diffRate.getUpdateDate());
    }

    public void setCurrentRate(ExchangeRate currentRate) {
        this.currentRate = currentRate;
    }

    public void setDiffRate(ExchangeRate diffRate) {
        this.diffRate = diffRate;
    }
}
