package app;

import configuration.ConfigFacade;
import exceptions.RequestException;
import exceptions.ValidationException;

import java.util.HashMap;

/**
 * Created by User on 13.05.2015.
 */
public class ActionBuilder implements IActionBuilder {

    private ConfigFacade facade;

    public ActionBuilder() {
        facade = ConfigFacade.getInstance();
    }

    @Override
    public AbstractAction buildActionObject(String ID, Context context) {
        try {
            Class resultClass = facade.getActionFactory().getActionClass(ID);
            HashMap<String, Object> actionProps = (HashMap<String, Object>) facade.getSystemProperty("action#id=?" + ID);
            validateAction(resultClass, context, actionProps);
            return facade.getActionFactory().createAction(ID, context);
        } catch (RequestException e) {
            e.printStackTrace();
        } catch (ValidationException e) {
            e.printStackTrace(); //TODO:
        }
        return null;
    }

    private void validateAction(Class actionClass, Context context, HashMap<String, Object> actionProps) throws ValidationException, RequestException {
        IValidatorFactory factory = facade.getValidatorFactory();
        factory.getValidator((String) facade.getSystemProperty("validator#id?SystemValidator")).validateContext(context, actionClass);
        factory.getValidator((String) actionProps.get("validatorID")).validateContext(context, actionClass);
    }
}
