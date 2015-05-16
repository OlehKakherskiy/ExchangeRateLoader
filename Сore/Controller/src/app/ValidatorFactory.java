package app;

import configuration.ConfigFacade;
import exceptions.RequestException;
import validators.AbstractValidator;

import java.util.HashMap;

/**
 * Created by User on 13.05.2015.
 */
public class ValidatorFactory implements IValidatorFactory{

    private HashMap<String, Object> validators = new HashMap<>();

    @Override
    public AbstractValidator getValidator(String ID) throws RequestException {
        String validatorClass = (String) validators.get(ID);
        if (validatorClass == null)
            throw new RequestException(String.format("Валідатора з ідентифікатором %s не існує", ID));
        else try {
            return (AbstractValidator) Class.forName(validatorClass).newInstance();
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RequestException("Системна помилка при створенні валідатора з ідентифікатором "+ ID+"\n"+e.getMessage());
        }
    }

    public ValidatorFactory() {
    }

    private void configureFactory() {
        HashMap<String, Object> result = (HashMap <String, Object>) ConfigFacade.getInstance().getSystemProperty("validatorList");
        for (String key : result.keySet()){
            validators.put(key.substring(key.indexOf('?')+1),result.get(key));
        }
    }
}
