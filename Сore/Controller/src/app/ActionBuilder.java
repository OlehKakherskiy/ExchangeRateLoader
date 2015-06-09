package app;

import annotation.ErrorParam;
import annotation.HasAlsoErrorsText;
import annotation.ShowError;
import configuration.ConfigFacade;
import exceptions.RequestException;
import exceptions.ValidationException;

import java.lang.reflect.Field;
import java.util.Map;

public class ActionBuilder implements IActionBuilder {

    private ConfigFacade facade;

    public ActionBuilder() {
    }

    private AbstractView requestView;

    @Override
    public AbstractAction buildActionObject(String ID, Context context) {
        if (facade == null) facade = ConfigFacade.getInstance();
        requestView = (AbstractView) context.getValue("requestView");
        try {
            if(!validateAction(facade.getActionFactory().getActionClass(ID), context, facade.getActionFactory().getActionProperties(ID)))
                return null;
            AbstractAction action = facade.getActionFactory().createAction(ID, context);
            return action;
        } catch (RequestException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (ValidationException e) {
            String exceptionMessage = findCustomExceptionMessage(context, e.getParamName(), requestView);
            if (exceptionMessage != null)
                ConfigFacade.getInstance().getViewFactory().showErrorMessage(exceptionMessage);
            else e.printStackTrace();
        }
        return null;
    }

    private boolean validateAction(Class actionClass, Context context, Map<String, Object> actionProps) throws ValidationException, RequestException {
        IValidatorFactory factory = facade.getValidatorFactory();
        factory.getValidator("SystemValidator").validateContext(context, actionClass);
        if(((String)actionProps.get("validatorID")).compareTo("SystemValidator") == 0)
            return true;
        return factory.getValidator((String) actionProps.get("validatorID")).validateContext(context, actionClass);
    }

    private String findCustomExceptionMessage(Context c, String paramName, AbstractView view) {
        if (paramName == null)
            return null;
        AbstractView requestView = (AbstractView) c.getValue("requestView");
        if (requestView == null)
            return null;
        return findExceptionText(requestView.getClass(), paramName, view);
    }

    private String findExceptionText(Class cl, String paramName, Object view) {
        ShowError annotation = (ShowError) cl.getAnnotation(ShowError.class);
        if (annotation == null) {
            Field[] fields = cl.getDeclaredFields();
            for (Field f : fields) {
                if (f.getAnnotation(HasAlsoErrorsText.class) != null) {
                    try {
                        f.setAccessible(true);
                        String result = findExceptionText(f.get(view).getClass(), paramName, f.get(view));
                        f.setAccessible(false);
                        if (result != null)
                            return result;
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
        for (ErrorParam param : annotation.list())
            if (param.contextElementName().compareTo(paramName) == 0)
                return param.errorMessage();
        return null;
    }
}
