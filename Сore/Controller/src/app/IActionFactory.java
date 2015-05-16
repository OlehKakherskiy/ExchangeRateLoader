package app;

import exceptions.RequestException;

/**
 * Created by User on 14.05.2015.
 */
public interface IActionFactory {

    Class getActionClass(String ID) throws RequestException;

    AbstractAction createAction(String ID, Context c) throws RequestException;
}
