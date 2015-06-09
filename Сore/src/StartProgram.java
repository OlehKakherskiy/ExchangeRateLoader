import app.*;
import configuration.ConfigFacade;
import entity.Bank;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

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

        AbstractView mainPane = ConfigFacade.getInstance().getViewFactory().createView("MainView");
        f.setSystemProperty("MainView", mainPane);
        Bank interBank = new Bank();
        interBank.setStorage((String) f.getSystemProperty("interBankDataStorage"));
        interBank.setName("средний курс по Украине");
        f.setSystemProperty("interBank", interBank);
        primaryStage.setScene(new Scene((Parent) mainPane));
        primaryStage.show();
        Context c = new Context();
        c.addValue("requestView", mainPane);
        Controller.getController().addRequest("StartAction", c);
        Map<String, Object> map = new HashMap<>();
        map.put("nextTabClosingPolicy", false);
        map.put("nextViewTitle", "Курс валют");
        mainPane.updateView(map);
    }
}
