package app;

/**
 * Created by User on 13.05.2015.
 */
public interface AbstractView<T> {

    void updateView(T data);

    void setNextView(AbstractView nextView);
}
