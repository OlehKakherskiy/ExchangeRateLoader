package viewController;

import app.AbstractView;
import configuration.ConfigFacade;
import entity.Bank;
import entity.BankList;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.*;

/**
 * Created by User on 05.06.2015.
 */
public class LineChartsPanel extends ScrollPane implements AbstractView<Map<String, Map<String, Map<LocalDate, Double>>>> {

    private AbstractView parent;

    @FXML
    private VBox vBox;

    @FXML
    private BorderPane borderPane;

    @FXML
    private ChooseHistoryParamsController historyParamsController;

    private List<Bank> banks;

    @Override
    public void updateView(Map<String, Map<String, Map<LocalDate, Double>>> data) {

        Map<String, LineChart<String, Number>> chartMap = new HashMap<>();

        Iterator iter = data.entrySet().iterator();
        if (iter.hasNext())
            for (String exchangeKey : data.get(iter.next()).keySet()) {
                LineChart<String, Number> chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
                chartMap.put(exchangeKey, chart);
                vBox.getChildren().add(chart);
            }

        for (String exchangeKey : chartMap.keySet())
            for (String banksName : data.keySet()) {
                XYChart.Series series = new XYChart.Series();
                series.setName(banksName);
                series.getData().addAll(getBanksExchangeRateData(data, exchangeKey, banksName));
                chartMap.get(exchangeKey).getData().add(series);
            }
        BankList bankList = (BankList) ConfigFacade.getInstance().getSystemProperty("bankList");
        Set<Bank> banks = new HashSet<>();
        for (String banksName: data.keySet())
            banks.add(bankList.getBankFromName(banksName));
        init();

    }

    private List<XYChart.Data> getBanksExchangeRateData(Map<String, Map<String, Map<LocalDate, Double>>> data, String exchangeKey, String banksName) {
        List<LocalDate> listDate = new ArrayList<>(data.get(banksName).get(exchangeKey).keySet());
        Collections.sort(listDate);
        List<XYChart.Data> resultList = new ArrayList<>();
        for (LocalDate date : listDate)
            resultList.add(new XYChart.Data<>(date.toString(), data.get(banksName).get(exchangeKey).get(date)));
        return resultList;
    }

    @Override
    public void setNextView(AbstractView nextView) {

    }

    @Override
    public void init() {
        historyParamsController.init();
        historyParamsController.setRequestSourceView(this);
        historyParamsController.setBanks(banks);
    }
}
