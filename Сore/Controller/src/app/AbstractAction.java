package app;

import java.util.concurrent.Callable;

/**
 * Created by User on 13.05.2015.
 */
public abstract class AbstractAction<V,T> implements Callable<V>{

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setResponseView(AbstractView responseView) {
        this.responseView = responseView;
    }

    protected Context context;

    protected AbstractView<T> responseView;
}
