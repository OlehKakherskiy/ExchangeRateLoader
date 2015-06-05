package app;

import exceptions.RequestException;
import validators.AbstractValidator;

/**
 * Created by User on 14.05.2015.
 */
public interface IValidatorFactory {

    AbstractValidator getValidator(String ID) throws RequestException;
}
