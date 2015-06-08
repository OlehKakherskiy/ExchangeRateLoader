package viewController;

import app.AbstractView;
import configuration.ConfigFacade;
import entity.Bank;
import entity.TableRowData;
import exceptions.RequestException;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
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
import java.util.Map;

public class TableViewController extends ScrollPane implements AbstractView<ObservableList<TableRowData>> {

    static {
        ConfigFacade facade = ConfigFacade.getInstance();
        exchangeNames = (Map<String, Object>) facade.getSystemProperty("exchangesToShow");
    }

    private static Map<String, Object> exchangeNames;

    @FXML
    TableView tableView;

    public Pane historyParamsPanel;

    @FXML
    BorderPane borderPane;

    @Override
    public void updateView(ObservableList<TableRowData> data) {
        tableView.setItems(data);
    }

    @Override
    public void setNextView(AbstractView nextView) {
        ((AbstractView) ConfigFacade.getInstance().getSystemProperty("MainView")).setNextView(nextView);
    }

    @Override
    public void init() throws RequestException {
        historyParamsPanel = (Pane) ConfigFacade.getInstance().getViewFactory().createView("HistoryParamsView");
        borderPane.setBottom(historyParamsPanel);
        Map<String, Object> map = new HashMap<>();
        map.put("requestSourceView", this);
        ((AbstractView) historyParamsPanel).updateView(map);
        BorderPane.setAlignment(borderPane.getBottom(), Pos.CENTER);
        TableColumn<TableRowData, Bank> banksNameColumn = new TableColumn<>("Название банка");
        banksNameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getBank()));
        TableColumn<TableRowData, LocalDate> dateColumn = new TableColumn<>("Дата обновления");
        dateColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getCurrentRate().getUpdateDate()));
        tableView.getColumns().addAll(banksNameColumn, dateColumn);
        tableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        for (String exchange : exchangeNames.keySet()) {
            String exchangeName = (String) exchangeNames.get(exchange);
            TableColumn column = new TableColumn(exchangeName);
            TableColumn<TableRowData, Number> buyColumn = new TableColumn<>("покупка");
            TableColumn<TableRowData, Number> saleColumn = new TableColumn<>("продажа");
            TableColumn<TableRowData, String> diffColumn = new TableColumn<>("изменение\nпокупка/продажа");

            diffColumn.setCellValueFactory(param1 -> new ReadOnlyObjectWrapper<>(getDifferenceRate(param1.getValue(), exchangeName)));
            buyColumn.setCellValueFactory(param1 -> new SimpleDoubleProperty(getExchangeRate(param1.getValue(), exchangeName, true)));
            saleColumn.setCellValueFactory(param1 -> new SimpleDoubleProperty(getExchangeRate(param1.getValue(), exchangeName, false)));
            column.getColumns().addAll(buyColumn, saleColumn, diffColumn);
            tableView.getColumns().addAll(column);
        }
    }


    private String getDifferenceRate(TableRowData value, String exchangeRateName) {
        StringBuilder builder = new StringBuilder();
        builder.append(getExchangeRate(value, exchangeRateName, true));
        builder.append("/").append(getExchangeRate(value, exchangeRateName, false));
        builder.append(" ").append(value.getRateDifference().getUpdateDate());
        return builder.toString();
    }

    private Double getExchangeRate(TableRowData value, String exchangeRateName, boolean buy) {
        String buyOrSale = buy == true ? "#buy" : "#sale";
        return Double.valueOf(String.format("%.3f",
                value.getCurrentRate().getRate().get(exchangeRateName.concat(buyOrSale)).doubleValue()).replace(',', '.'));
    }
}
