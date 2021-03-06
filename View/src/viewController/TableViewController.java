package viewController;

import annotation.HasAlsoErrorsText;
import app.AbstractView;
import configuration.ConfigFacade;
import entity.Bank;
import entity.ExchangeRate;
import entity.TableRowData;
import exceptions.RequestException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableViewController extends ScrollPane implements AbstractView<Map<String, Object>> {

    static {
        ConfigFacade facade = ConfigFacade.getInstance();
        exchangeNames = (Map<String, Object>) facade.getSystemProperty("exchangesToShow");
    }

    private static Map<String, Object> exchangeNames;

    @FXML
    TableView tableView;

    @HasAlsoErrorsText
    public AbstractView<Map<String,Object>> historyParamsPanel;

    @FXML
    BorderPane borderPane;

    @FXML
    private Pane interBankRate;

    @FXML
    private InterBankViewController interBankRateController;

    @Override
    public void updateView(Map<String, Object> data) {
        if (data.get("tableRowData") != null)
            tableView.setItems(FXCollections.observableList((List<TableRowData>) data.get("tableRowData")));
        if (data.get("currentRate") != null)
            interBankRateController.setCurrentRate((ExchangeRate) data.get("currentRate"));
        if (data.get("diffRate") != null)
            interBankRateController.setDiffRate((ExchangeRate) data.get("diffRate"));
    }

    @Override
    public void setNextView(AbstractView nextView) {
        Map<String, Object> map = new HashMap<>();
        map.put("nextTabClosingPolicy", true);
        map.put("nextViewTitle", "Історія");
        ((AbstractView) ConfigFacade.getInstance().getSystemProperty("MainView")).updateView(map);
        ((AbstractView) ConfigFacade.getInstance().getSystemProperty("MainView")).setNextView(nextView);
    }

    @Override
    public void init() throws RequestException {
        historyParamsPanel = ConfigFacade.getInstance().getViewFactory().createView("HistoryParamsView");
        borderPane.setBottom((Pane)historyParamsPanel);
        Map<String, Object> map = new HashMap<>();
        map.put("requestSourceView", this);
        historyParamsPanel.updateView(map);
        BorderPane.setAlignment(borderPane.getBottom(), Pos.CENTER);
        TableColumn<TableRowData, Bank> banksNameColumn = new TableColumn<>("Назва банку");
        banksNameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getBank()));
        TableColumn<TableRowData, LocalDate> dateColumn = new TableColumn<>("Дата оновлення");
        dateColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getCurrentRate().getUpdateDate()));
        tableView.getColumns().addAll(banksNameColumn, dateColumn);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        for (String exchange : exchangeNames.keySet()) {
            String exchangeName = (String) exchangeNames.get(exchange);
            TableColumn column = new TableColumn(exchangeName);
            TableColumn<TableRowData, Number> buyColumn = new TableColumn<>("купівля");
            TableColumn<TableRowData, Number> saleColumn = new TableColumn<>("продаж");
            TableColumn<TableRowData, String> diffColumn = new TableColumn<>("зміна\nкупівля/продаж");

            diffColumn.setCellValueFactory(param1 -> new ReadOnlyObjectWrapper<>(getDifferenceRate(param1.getValue(), exchangeName)));
            buyColumn.setCellValueFactory(param1 -> new SimpleDoubleProperty(getExchangeRate(param1.getValue(), exchangeName, true, false)));
            saleColumn.setCellValueFactory(param1 -> new SimpleDoubleProperty(getExchangeRate(param1.getValue(), exchangeName, false, false)));
            column.getColumns().addAll(buyColumn, saleColumn, diffColumn);
            tableView.getColumns().addAll(column);
        }
    }


    private String getDifferenceRate(TableRowData value, String exchangeRateName) {
        StringBuilder builder = new StringBuilder();
        builder.append(getExchangeRate(value, exchangeRateName, true, true));
        builder.append("/").append(getExchangeRate(value, exchangeRateName, false, true));
        builder.append(" ").append(value.getRateDifference().getUpdateDate());
        return builder.toString();
    }

    private Double getExchangeRate(TableRowData value, String exchangeRateName, boolean buy, boolean differenceRate) {
        String buyOrSale = buy == true ? "#buy" : "#sale";
        if (differenceRate)
            return Double.valueOf(String.format("%.3f", value.getRateDifference().getRate().
                    get(exchangeRateName.concat(buyOrSale)).doubleValue()).replace(',', '.'));
        return Double.valueOf(String.format("%.3f",
                value.getCurrentRate().getRate().get(exchangeRateName.concat(buyOrSale)).doubleValue()).replace(',', '.'));
    }
}
