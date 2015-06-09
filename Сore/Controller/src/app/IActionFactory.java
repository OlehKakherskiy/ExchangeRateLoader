package app;

import exceptions.RequestException;

import java.util.Map;


public interface IActionFactory {

    Class getActionClass(String ID) throws RequestException, ClassNotFoundException;

    AbstractAction createAction(String ID, Context c) throws RequestException;

    Map<String, Object> getActionProperties(String ID) throws RequestException;
}
