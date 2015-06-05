package app;

import configuration.ConfigFacade;
import exceptions.RequestException;
import exceptions.ValidationException;

import java.util.Map;

/**
 * Created by User on 13.05.2015.
 */
public class ActionBuilder implements IActionBuilder {

    private ConfigFacade facade;

    public ActionBuilder() {}

    @Override
    public AbstractAction buildActionObject(String ID, Context context) {
        if(facade == null) facade = ConfigFacade.getInstance();
        try {
            validateAction(facade.getActionFactory().getActionClass(ID), context, facade.getActionFactory().getActionProperties(ID));
            return facade.getActionFactory().createAction(ID, context);
        } catch (RequestException e) {
            e.printStackTrace();
        } catch (ValidationException e) {
            e.printStackTrace(); //TODO:
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void validateAction(Class actionClass, Context context, Map<String, Object> actionProps) throws ValidationException, RequestException {
        IValidatorFactory factory = facade.getValidatorFactory();
        factory.getValidator("SystemValidator").validateContext(context, actionClass);
        factory.getValidator((String) actionProps.get("validatorID")).validateContext(context, actionClass);
    }
}
