package validators;

import app.AbstractAction;
import app.Context;
import exceptions.ValidationException;

/**
 * Created by User on 13.05.2015.
 */
public abstract class AbstractValidator {

    public abstract void validateContext(Context context, Class<AbstractAction> action) throws ValidationException;

}
