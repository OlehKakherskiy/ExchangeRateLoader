package app;

import configuration.ConfigFacade;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Controller {

    private ExecutorService queue = Executors.newSingleThreadExecutor();

    private static final Controller c = new Controller();

    public void addRequest(String actionID, Context c){
        AbstractAction action = ConfigFacade.getInstance().getActionBuilder().buildActionObject(actionID, c);
        if(action == null)
            return;
        queue.submit(action);
    }

    public static Controller getController(){
        return c;
    }
}
