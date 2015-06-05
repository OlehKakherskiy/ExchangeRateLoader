import app.IActionBuilder;
import app.IActionFactory;
import app.IValidatorFactory;
import app.IViewFactory;
import configuration.ConfigFacade;

/**
 * Created by User on 15.05.2015.
 */
public class StartProgram {

    public static void main(String[] args) {
        ConfigFacade f = ConfigFacade.getInstance();
        try {
            f.setActionBuilder((IActionBuilder) Class.forName((String) f.getSystemProperty("actionBuilder")).newInstance());
            f.setViewFactory((IViewFactory) Class.forName((String) f.getSystemProperty("viewFactory")).newInstance());
            f.setValidatorFactory((IValidatorFactory) Class.forName((String) f.getSystemProperty("validatorFactory")).newInstance());
            f.setActionFactory((IActionFactory) Class.forName((String) f.getSystemProperty("actionFactory")).newInstance());
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        app.AbstractAction startAction = f.getActionBuilder().buildActionObject("StartAction", null);
        startAction.run();
    }
}
