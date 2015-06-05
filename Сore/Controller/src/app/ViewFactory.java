package app;

import exceptions.RequestException;
import javafx.fxml.FXMLLoader;

import java.io.IOException;
import java.util.HashMap;

public class ViewFactory implements IViewFactory {

    private HashMap<String, Object> views = new HashMap<>();

    public ViewFactory() {
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
            return loader.load();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            throw new RequestException();
        } catch (IOException e) {
            throw new RequestException();
        }
    }
}
