package app;

import exceptions.RequestException;

/**
 * Created by User on 14.05.2015.
 */
public interface IViewFactory {

    AbstractView createView(String ID) throws RequestException;

}
