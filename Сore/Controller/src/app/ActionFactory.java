package app;

import configuration.ConfigFacade;
import exceptions.RequestException;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 13.05.2015.
 */
public class ActionFactory implements IActionFactory {

    private HashMap<String, Object> readyToInstanceActions;

    public ActionFactory() {
        readyToInstanceActions = new HashMap<>();
        configureFactory();
    }

    public void configureFactory() {
        HashMap<String, Object> result = (HashMap<String, Object>) ConfigFacade.getInstance().getSystemProperty("actionList");
        for (String key : result.keySet()) {
            result.put("needResponseView", key.contains("true") ? true : false);
            readyToInstanceActions.put(key.substring(key.indexOf('?') + 1), result.get(key));
        }
    }

    @Override
    public Class getActionClass(String ID) throws RequestException, ClassNotFoundException {
        Class result = Class.forName((String) ((HashMap<String, Object>) readyToInstanceActions.get(ID)).get("class"));
        if (result == null)
            throw new RequestException(String.format("Класу - команди з ID = %s не існує", ID));
        return result;
    }

    @Override
    public AbstractAction createAction(String ID, Context c) throws RequestException {
        try {
            HashMap<String, Object> props = ((HashMap<String, Object>) readyToInstanceActions.get(ID));
            Class actionClass = Class.forName((String) props.get("class"));
            AbstractAction result = (AbstractAction) actionClass.newInstance();
            if ((Boolean) props.get("needResponseView") == true) {
                if(props.get("responseView") == null)
                    throw new RequestException(); //TODO:
                result.setResponseView(ConfigFacade.getInstance().getViewFactory().createView((String) props.get("viewID")));
            }
            result.setContext(c);
            return result;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new RequestException(e.getMessage());
        }
    }

    @Override
    public Map<String, Object> getActionProperties(String ID) throws RequestException {
        Map<String, Object> result = (Map<String, Object>) readyToInstanceActions.get(ID);
        if (result == null)
            throw new RequestException();
        return result;
    }
}
