package app;

/**
 * Created by User on 14.05.2015.
 */
public interface IActionBuilder {

    AbstractAction buildActionObject(String ID, Context context);
}
