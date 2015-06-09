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
        System.out.println(exchangeNames);
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
            if(currentRate == null || diffRate == null)
                return;
            rateChanging.setText(getDiffRate(newValue));
            exchangeRateValue.setText(getRate(newValue));
        });
    }

    private ExchangeRate currentRate;

    private ExchangeRate diffRate;

    private String getRate(String exchangeName) {
        StringBuilder builder = new StringBuilder();
        builder.append(currentRate.getRate().get(exchangeName + "#buy")).append("/").
                append(currentRate.getRate().get(exchangeName + "#sale"));
        return builder.toString();
    }

    private String getDiffRate(String exchangeName) {
        StringBuilder builder = new StringBuilder();
        builder.append(diffRate.getRate().get(exchangeName + "#buy")).append("/").
                append(diffRate.getRate().get(exchangeName + "#sale")).append(" ").append(diffRate.getUpdateDate());
        return builder.toString();
    }

    public void setCurrentRate(ExchangeRate currentRate) {
        this.currentRate = currentRate;
    }

    public void setDiffRate(ExchangeRate diffRate) {
        this.diffRate = diffRate;
    }
}
