package actions;

import annotation.Action;
import annotation.ContextAnnotation;
import annotation.Parameter;
import app.AbstractAction;
import app.AbstractView;
import entity.Bank;
import entity.DateCount;

@Action(updatesView = true)
@ContextAnnotation(list = {
        @Parameter(key = "period", type = DateCount.class),
        @Parameter(key = "buy", type = Boolean.class),
        @Parameter(key = "sale", type = Boolean.class),
        @Parameter(key = "bank", type = Bank.class),
        @Parameter(key = "requestView", type = AbstractView.class)
})
public class ShowExchangeHistoryAction extends AbstractAction {

    @Override
    public void run() {

    }
}
