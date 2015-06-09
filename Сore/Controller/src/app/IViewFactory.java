package app;

import exceptions.RequestException;

public interface IViewFactory {

    AbstractView createView(String ID) throws RequestException;

    void showErrorMessage(String message);
}
