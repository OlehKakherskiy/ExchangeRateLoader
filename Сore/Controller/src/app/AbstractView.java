package app;

/**
 * Created by User on 13.05.2015.
 */
public interface AbstractView {

    void updateView(Object data);

    void setNextView(AbstractView nextView);
}
