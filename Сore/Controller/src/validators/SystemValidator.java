package validators;

import annotation.Action;
import annotation.ContextAnnotation;
import annotation.Parameter;
import app.AbstractAction;
import app.Context;
import exceptions.ValidationException;

/**
 * Created by User on 13.05.2015.
 */
public class SystemValidator extends AbstractValidator {

    @Override
    public void validateContext(Context context, Class<AbstractAction> action) throws ValidationException {
        Action actionAnnotation = action.getAnnotation(Action.class);
        if (actionAnnotation == null)
            throw new ValidationException("Системна помилка.Команда класу " + action.getCanonicalName() + "має бути помічена анотацію Action");
        if (!actionAnnotation.needContext())
            return;
        if (actionAnnotation.needContext() && context == null)
            throw new ValidationException("Системна помилка. Для коректної роботи команди класу " + action.getCanonicalName() +
                    " необхідно передати контекст.");
        ContextAnnotation contextAnnotation = action.getAnnotation(ContextAnnotation.class);
        chechEveryParameter(context, contextAnnotation, action);
    }

    private void chechEveryParameter(Context context, ContextAnnotation contextAnnotation, Class<AbstractAction> action) throws ValidationException {
        for (Parameter param : contextAnnotation.list()) {
            String key = param.key();
            Object value = context.getValue(key);
            if (value == null && !param.optional())
                throw new ValidationException("В контекст команди класу " + action.getCanonicalName() +
                        " має бути передане значення з ключем " + param.key());
            if (!param.type().isAssignableFrom(value.getClass()))
                throw new ValidationException(String.format("В контексті команди класу %s параметр %s має бути типом %s,а не %s",
                        action.getCanonicalName(), key, param.type(), value.getClass()));
        }
    }
}
