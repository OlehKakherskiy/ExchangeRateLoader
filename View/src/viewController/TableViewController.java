package viewController;

import app.AbstractView;
import configuration.ConfigFacade;
import entity.Bank;
import entity.TableRowData;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.util.List;

public class TableViewController implements AbstractView<List<TableRowData>> {

    static {
        ConfigFacade facade = ConfigFacade.getInstance();
        exchangeNames = (List<String>) facade.getSystemProperty("exchangesToShow");
    }

    private static List<String> exchangeNames;

    @FXML
    private TableView tableView;

    @FXML
    private ChooseHistoryParamsController historyParamsController;

    public TableViewController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../exchangeRateTableView.fxml"));
        loader.setRoot(this);
        loader.setController(this);
        try {
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TableColumn<TableRowData, Bank> banksNameColumn = new TableColumn<>("Название банка");
        banksNameColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getBank()));
//        TableColumn<TableRowData,LocalDate> dateColumn = new TableColumn<>("")
        for (String exchange : exchangeNames) {
            TableColumn column = new TableColumn(exchange);
            TableColumn<TableRowData, Double> buyColumn = new TableColumn<>();
            TableColumn<TableRowData, Double> saleColumn = new TableColumn<>();
            buyColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getCurrentRate().getRate().get(exchange.concat("#buy"))));
            saleColumn.setCellValueFactory(param -> new ReadOnlyObjectWrapper<>(param.getValue().getCurrentRate().getRate().get(exchange.concat("#sale"))));
            column.getColumns().add(buyColumn);
            column.getColumns().add(saleColumn);
            tableView.getColumns().add(column);
        }
    }

    @Override
    public void updateView(List<TableRowData> data) {
        tableView.setItems(FXCollections.observableList(data));
    }

    @Override
    public void setNextView(AbstractView nextView) {
        ((AbstractView) ConfigFacade.getInstance().getSystemProperty("MainView")).setNextView(nextView);
    }
}
