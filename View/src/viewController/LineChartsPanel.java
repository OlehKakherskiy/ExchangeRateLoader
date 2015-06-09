package viewController;

import annotation.HasAlsoErrorsText;
import app.AbstractView;
import configuration.ConfigFacade;
import entity.Bank;
import entity.BankList;
import exceptions.RequestException;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
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

    @HasAlsoErrorsText
    private Pane historyParamsController;

    private List<Bank> banks;

    @Override
    public void updateView(Map<String, Map<String, Map<LocalDate, Double>>> data) {

        Map<String, LineChart<String, Number>> chartMap = new HashMap<>();
        Iterator<Map.Entry<String, Map<String, Map<LocalDate, Double>>>> iter = data.entrySet().iterator();

        if (iter.hasNext()) {
            Map.Entry<String, Map<String, Map<LocalDate, Double>>> next = iter.next();
            for (String exchangeKey : data.get(next.getKey()).keySet()) {
                LineChart<String, Number> chart = new LineChart<>(new CategoryAxis(), new NumberAxis());
                chart.setTitle(makeChartTitle(exchangeKey));
                chartMap.put(exchangeKey, chart);
                vBox.getChildren().add(chart);
            }
        }
        for (String exchangeKey : chartMap.keySet()) {
            for (String banksName : data.keySet()) {
                XYChart.Series series = new XYChart.Series();
                series.setName(banksName);
                series.getData().addAll(getBanksExchangeRateData(data, exchangeKey, banksName));
                chartMap.get(exchangeKey).getData().add(series);
            }
        }
        BankList bankList = (BankList) ConfigFacade.getInstance().getSystemProperty("bankList");
        List<Bank> banks = new ArrayList<>();
        for (String banksName : data.keySet()) {
            Bank b = bankList.getBankFromName(banksName);
            if(b == null){
                b = (Bank) ((Map<String,Object>)ConfigFacade.getInstance().getSystemProperty("interBank")).get("interBank");
            }
            banks.add(b);
        }
        Map<String, Object> params = new HashMap<>();
        params.put("banks", banks);
        params.put("requestSourceView", this);
        ((AbstractView) historyParamsController).updateView(params);
    }

    private String makeChartTitle(String key) {
        int specSymbol = key.indexOf('#');
        if (specSymbol == -1)
            return "";
        StringBuilder builder = new StringBuilder(key.substring(0, specSymbol));
        if (key.substring(specSymbol + 1).compareTo("buy") == 0)
            builder.append(" купівля");
        else builder.append(" продаж");
        return builder.toString();
    }

    private List<XYChart.Data> getBanksExchangeRateData(Map<String, Map<String, Map<LocalDate, Double>>> data, String exchangeKey, String banksName) {
        List<LocalDate> listDate = new ArrayList<>(data.get(banksName).get(exchangeKey).keySet());
        Collections.sort(listDate);
        List<XYChart.Data> resultList = new ArrayList<>();
        for (LocalDate date : listDate)
            resultList.add(addNewData(date.toString(), data.get(banksName).get(exchangeKey).get(date), banksName));
        return resultList;
    }

    private XYChart.Data addNewData(String x, Number y, String banksName) {
        XYChart.Data newData = new XYChart.Data(x, y);
        newData.setNode(new StackPane());
        Tooltip tooltip = new Tooltip(String.format("%s = [%s,%s]", banksName, x, y));
        Tooltip.install(newData.getNode(), tooltip);
        return newData;
    }

    @Override
    public void setNextView(AbstractView nextView) {
        Map<String, Object> map = new HashMap<>();
        map.put("nextTabClosingPolicy", true);
        map.put("nextViewTitle", "Історія");
        map.put("closeLastAddedTag", true);
        ((AbstractView) ConfigFacade.getInstance().getSystemProperty("MainView")).updateView(map);
        ((AbstractView) ConfigFacade.getInstance().getSystemProperty("MainView")).setNextView(nextView);
    }

    @Override
    public void init() throws RequestException {
        historyParamsController = (Pane) ConfigFacade.getInstance().getViewFactory().createView("HistoryParamsView");
        borderPane.setBottom(historyParamsController);
        BorderPane.setAlignment(borderPane.getBottom(), Pos.CENTER);
        ((AbstractView) historyParamsController).init();
    }
}
