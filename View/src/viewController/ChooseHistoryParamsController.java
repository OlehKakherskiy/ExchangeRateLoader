package viewController;

import app.AbstractView;
import app.Context;
import app.Controller;
import entity.Bank;
import entity.DateCount;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ToggleGroup;

public class ChooseHistoryParamsController {

    @FXML
    private ToggleGroup historyGroup;

    @FXML
    private CheckBox buy;

    @FXML
    private CheckBox sale;

    @FXML
    private Button showHistoryButton;

    private AbstractView requestSourceView;

    private Bank currentBank;

    @FXML
    private void initialize() {
        historyGroup.getToggles().get(0).setUserData(DateCount.Week);
        historyGroup.getToggles().get(1).setUserData(DateCount.Month);
        historyGroup.getToggles().get(2).setUserData(DateCount.ThreeMonth);
        historyGroup.getToggles().get(3).setUserData(DateCount.SixMonth);
        historyGroup.getToggles().get(4).setUserData(DateCount.Year);
        historyGroup.getToggles().get(5).setUserData(DateCount.AllTime);
    }

    @FXML
    private void showExchangeHistory() {
        Context c = new Context();
        c.addValue("buy", buy.isSelected());
        c.addValue("sale", buy.isSelected());
        c.addValue("period", historyGroup.getSelectedToggle().getUserData());
        c.addValue("requestView", requestSourceView);
        c.addValue("bank", currentBank);
        Controller.getController().addRequest("ShowExchangeHistory", c);
    }

    public AbstractView getRequestSourceView() {
        return requestSourceView;
    }

    public void setRequestSourceView(AbstractView requestSourceView) {
        this.requestSourceView = requestSourceView;
    }

    public Bank getCurrentBank() {
        return currentBank;
    }

    public void setCurrentBank(Bank currentBank) {
        this.currentBank = currentBank;
    }
}
