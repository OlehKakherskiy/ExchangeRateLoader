package viewController;

import app.AbstractView;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

import java.util.Map;

/**
 * Created by User on 05.06.2015.
 */
public class MainViewController extends TabPane implements AbstractView<Map<String, Object>> {

    @FXML
    private TabPane tabPane;

    private String nextViewTitle;

    private boolean nextTabClosingPolicy;

    private boolean closeLastAddedTag;

    @Override
    public void updateView(Map<String, Object> data) {
        nextTabClosingPolicy = (boolean) data.get("nextTabClosingPolicy");
        nextViewTitle = (String) data.get("nextViewTitle");
        if (data.get("closeLastAddedTag") != null)
            closeLastAddedTag = (boolean)data.get("closeLastAddedTag");

    }

    public MainViewController() {
    }

    @Override
    public void setNextView(AbstractView nextView) {
        Platform.runLater(() -> {
            Tab tab = new Tab(nextViewTitle);
            tab.setClosable(nextTabClosingPolicy);
            BorderPane borderPane = new BorderPane((Node) nextView);
            if (closeLastAddedTag)
                tabPane.getTabs().remove(tabPane.getTabs().size() - 1);
            tabPane.getTabs().add(tab);
            AnchorPane.setBottomAnchor(borderPane, 0.0);
            AnchorPane.setLeftAnchor(borderPane, 0.0);
            AnchorPane.setRightAnchor(borderPane, 0.0);
            AnchorPane.setTopAnchor(borderPane, 0.0);

            AnchorPane pane = new AnchorPane(borderPane);
            tab.contentProperty().setValue(pane);
            tabPane.getSelectionModel().select(tab);
        });
    }

    @Override
    public void init() {
        tabPane.setTabClosingPolicy(TabClosingPolicy.SELECTED_TAB);
    }
}
