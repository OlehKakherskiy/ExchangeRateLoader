package app;

import exceptions.RequestException;

import java.util.HashMap;

public class ViewFactory implements IViewFactory{

    private HashMap<String, Object> views = new HashMap<>();

    public ViewFactory(){}

    @Override
    public AbstractView createView(String ID) throws RequestException {
        HashMap<String,Object> params = (HashMap<String, Object>) views.get(ID);
//        if(params.size() == 0)
//            throw new RequestException("Об'єкту-відображення з ID = "+ID+" не існує");
        String viewSource = (String) params.get("source");
        //TODO: тут javafx!!
        try {
            return (AbstractView) Class.forName(null).newInstance(); //TODO: null убрать
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
