package app;

import configuration.ConfigFacade;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by User on 13.05.2015.
 */
public class Controller {

    private ExecutorService queue = Executors.newSingleThreadExecutor();

    public void addRequest(String actionID, Context c){
        AbstractAction action = ConfigFacade.getInstance().getActionBuilder().buildActionObject(actionID, c);
        queue.submit(action);
    }
}
