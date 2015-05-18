package app;

import java.util.concurrent.Callable;

/**
 * Created by User on 13.05.2015.
 */
public abstract class AbstractAction implements Runnable, Callable {

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public AbstractView getResponseView() {
        return responseView;
    }

    public void setResponseView(AbstractView responseView) {
        this.responseView = responseView;
    }

    protected Context context;

    private AbstractView responseView;

    @Override
    public void run() {
    }

    @Override
    public Object call(){return null;}
}
