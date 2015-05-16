package app;

import configuration.ConfigFacade;
import exceptions.RequestException;

import java.util.HashMap;

/**
 * Created by User on 13.05.2015.
 */
public class ActionFactory implements IActionFactory {

    private HashMap<String, Object> readyToInstanceActions;

    public ActionFactory() {
        readyToInstanceActions = new HashMap<>();
    }

    private void configureFactory() {
        HashMap<String, Object> result = (HashMap < String, Object >)ConfigFacade.getInstance().getSystemProperty("actionList");
        for (String key : result.keySet()){
            readyToInstanceActions.put(key.substring(key.indexOf('?')+1),result.get(key));
        }
    }

    @Override
    public Class getActionClass(String ID) throws RequestException {
        Class result = (Class) ((HashMap<String, Object>) readyToInstanceActions.get(ID)).get("class");
        if (result == null)
            throw new RequestException(String.format("Класу - команди з ID = %s не існує", ID));
        return result;
    }

    @Override
    public AbstractAction createAction(String ID, Context c) throws RequestException {
        try {
            HashMap<String, Object> props = ((HashMap<String, Object>) readyToInstanceActions.get(ID));
            Class actionClass = (Class) props.get("class");
            AbstractAction result = (AbstractAction) actionClass.newInstance();
            result.setResponseView(ConfigFacade.getInstance().getViewFactory().createView((String) props.get("viewID")));
            result.setContext(c);
            return result;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RequestException(e.getMessage());
        }
    }
}
