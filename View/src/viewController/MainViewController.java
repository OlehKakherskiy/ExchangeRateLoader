package viewController;

import app.AbstractView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

/**
 * Created by User on 05.06.2015.
 */
public class MainViewController extends TabPane implements AbstractView{

    @FXML
    private TabPane tabPane;

    @Override
    public void updateView(Object data) {}

    public MainViewController(){}

    @Override
    public void setNextView(AbstractView nextView) {
       Platform.runLater(() -> {
           Tab tab = new Tab();
           tabPane.getTabs().add(tab);
           BorderPane borderPane = new BorderPane((Node) nextView);

           AnchorPane.setBottomAnchor(borderPane, 0.0);
           AnchorPane.setLeftAnchor(borderPane, 0.0);
           AnchorPane.setRightAnchor(borderPane, 0.0);
           AnchorPane.setTopAnchor(borderPane, 0.0);

           AnchorPane pane = new AnchorPane(borderPane);
           tab.contentProperty().setValue(pane);
       });
    }

    @Override
    public void init() {

    }
}
