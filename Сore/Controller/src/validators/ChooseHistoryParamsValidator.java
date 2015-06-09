package validators;

import app.AbstractAction;
import app.Context;
import configuration.ConfigFacade;
import exceptions.ValidationException;

/**
 * Created by User on 09.06.2015.
 */
public class ChooseHistoryParamsValidator extends AbstractValidator {

    @Override
    public boolean validateContext(Context context, Class<AbstractAction> action) throws ValidationException {
        if (!(Boolean) context.getValue("readBuyValue") && !(Boolean) context.getValue("readSaleValue")) {
            ConfigFacade.getInstance().getViewFactory().showErrorMessage("Оберіть тип операції: купівлю або продаж");
            return false;
        }
        return true;
    }
}
