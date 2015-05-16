package app;

/**
 * Created by User on 13.05.2015.
 */
public abstract class AbstractAction implements Runnable {

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

    private Context context;

    private AbstractView responseView;

    @Override
    public void run() {
    }

}
