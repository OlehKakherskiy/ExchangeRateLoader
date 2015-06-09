package viewController;

import app.AbstractView;
import app.Context;
import app.Controller;
import configuration.ConfigFacade;
import entity.Bank;
import entity.DateCount;
import entity.TableRowData;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseHistoryParamsController extends Pane implements AbstractView<HashMap<String, Object>> {

    @FXML
    private ToggleGroup historyGroup;

    @FXML
    private CheckBox buy;

    @FXML
    private CheckBox sale;

    @FXML
    private Button showHistoryButton;

    @FXML
    private HBox exchangeNameBox;

    @FXML
    private CheckBox interBankRate;

    private AbstractView requestSourceView;

    private List<Bank> banks;


    private void showExchangeHistory() {
        Context c = new Context();
        c.addValue("readBuyValue", buy.isSelected());
        c.addValue("readSaleValue", sale.isSelected());
        if (historyGroup.getSelectedToggle() != null)
            c.addValue("dateCount", historyGroup.getSelectedToggle().getUserData());
        c.addValue("requestView", requestSourceView);

        if (requestSourceView instanceof TableViewController) {
            banks = new ArrayList<>();
            List<TableRowData> list = ((TableViewController) requestSourceView).tableView.getSelectionModel().getSelectedItems();
            for (TableRowData data : list)
                banks.add(data.getBank());
        }

        int interBankPos = hasInterBank();
        if (interBankRate.isSelected() && interBankPos == -1) {
            Map<String, Object> map = (Map<String, Object>) ConfigFacade.getInstance().getSystemProperty("interBank");
            banks.add((Bank) map.get("interBank"));
        } else if (!interBankRate.isSelected() && interBankPos != -1) {
            banks.remove(interBankPos);
        }
        c.addValue("banks", banks);
        List<String> exchangeNames = new ArrayList<>();
        for (Node node : exchangeNameBox.getChildren()) {
            CheckBox checkBox = (CheckBox) node;
            if (checkBox.isSelected())
                exchangeNames.add(checkBox.getText());
        }
        c.addValue("exchangeNames", exchangeNames);
        c.addValue("startDate", LocalDate.now());
        Controller.getController().addRequest("ShowExchangeHistory", c);
        banks = null;
    }

    private int hasInterBank() {
        if (banks == null || banks.size() == 0)
            return -1;
        else {
            int iterator = 0;
            for (Bank b : banks) {
                if (b.getStorage().compareTo((String) ConfigFacade.getInstance().getSystemProperty("interBankDataStorage")) == 0)
                    return iterator;
                iterator++;
            }
            return -1;
        }
    }

    public AbstractView getRequestSourceView() {
        return requestSourceView;
    }

    public void setRequestSourceView(AbstractView requestSourceView) {
        this.requestSourceView = requestSourceView;
    }

    public void setBanks(List<Bank> banks) {
        this.banks = banks;
    }

    @Override
    public void updateView(HashMap<String, Object> data) {
        if (data.get("banks") != null)
            banks = (List<Bank>) data.get("banks");
        if (data.get("requestSourceView") != null)
            requestSourceView = (AbstractView) data.get("requestSourceView");
    }

    @Override
    public void setNextView(AbstractView nextView) {

    }


    @Override
    public void init() {
        showHistoryButton.setOnAction(event -> showExchangeHistory());
        historyGroup.getToggles().get(0).setUserData(DateCount.Week);
        historyGroup.getToggles().get(1).setUserData(DateCount.Month);
        historyGroup.getToggles().get(2).setUserData(DateCount.ThreeMonth);
        historyGroup.getToggles().get(3).setUserData(DateCount.SixMonth);
        historyGroup.getToggles().get(4).setUserData(DateCount.Year);
        historyGroup.getToggles().get(5).setUserData(DateCount.AllTime);

        Map<String, Object> exchangesToShow = (Map<String, Object>) ConfigFacade.getInstance().getSystemProperty("exchangesToShow");
        if (exchangeNameBox.getChildren().size() != 0)
            return;
        for (String exchangeKey : exchangesToShow.keySet()) {
            CheckBox checkBox = new CheckBox((String) exchangesToShow.get(exchangeKey));
            exchangeNameBox.setMargin(checkBox, new Insets(0, 10, 0, 0));
            exchangeNameBox.getChildren().add(checkBox);
        }
    }
}
