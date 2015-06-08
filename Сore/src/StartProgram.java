import app.*;
import configuration.ConfigFacade;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by User on 15.05.2015.
 */
public class StartProgram extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ConfigFacade f = ConfigFacade.getInstance();
        primaryStage.setOnCloseRequest(event -> System.exit(0));
        try {
            f.setActionBuilder((IActionBuilder) Class.forName((String) f.getSystemProperty("actionBuilder")).newInstance());
            f.setViewFactory((IViewFactory) Class.forName((String) f.getSystemProperty("viewFactory")).newInstance());
            f.setValidatorFactory((IValidatorFactory) Class.forName((String) f.getSystemProperty("validatorFactory")).newInstance());
            f.setActionFactory((IActionFactory) Class.forName((String) f.getSystemProperty("actionFactory")).newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }

//        ShowExchangeHistoryAction.main(null);
        AbstractView mainPane = ConfigFacade.getInstance().getViewFactory().createView("MainView");

        primaryStage.setScene(new Scene((Parent) mainPane));
        primaryStage.show();
        Context c = new Context();
        c.addValue("requestView", mainPane);
        Controller.getController().addRequest("StartAction", c);
    }
}
