package app;

import configuration.ConfigFacade;
import exceptions.RequestException;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class FxViewFactory implements IViewFactory {

    private HashMap<String, Object> views = new HashMap<>();

    public FxViewFactory() {
        Map<String, Object> map = (Map<String, Object>) ConfigFacade.getInstance().getSystemProperty("viewList");
        for (String key : map.keySet()) {
            ((Map<String, Object>) map.get(key)).put("getFromSource", key.contains("true") ? true : false);
            int indexFrom = key.indexOf("#id?") + 4;
            int indexTo = key.indexOf('#', indexFrom);
            if (indexTo == -1) {
                views.put(key.substring(indexFrom), map.get(key));
            } else views.put(key.substring(indexFrom, indexTo), map.get(key));
        }
    }

    @Override
    public AbstractView createView(String ID) throws RequestException {
        try {
            HashMap<String, Object> params = (HashMap<String, Object>) views.get(ID);
            if (params.size() == 0)
                throw new RequestException("Об'єкту-відображення з ID = " + ID + " не існує");
            String viewSource = (String) params.get("source");
            Class sourceClass = Class.forName((String) params.get("class"));
            FXMLLoader loader = new FXMLLoader(sourceClass.getResource(viewSource));
            AbstractView viewController = (AbstractView) sourceClass.newInstance();

            loader.setController(viewController);
            loader.setRoot(viewController);
            loader.load();
            viewController.init();
            return viewController;
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RequestException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RequestException();
        }
    }
}
