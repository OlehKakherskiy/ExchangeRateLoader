package validators;

import app.AbstractAction;
import app.Context;
import exceptions.ValidationException;

public abstract class AbstractValidator {

    public abstract boolean validateContext(Context context, Class<AbstractAction> action) throws ValidationException;

}
