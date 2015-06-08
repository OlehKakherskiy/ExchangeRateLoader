package actions;

import app.AbstractAction;

import java.util.Map;

/**
 * Created by User on 17.05.2015.
 */
public abstract class AbstractRowParser extends AbstractAction<Map<String, Double>,Void> {

    @Override
    public Map<String, Double> call() throws Exception{
        return parseTable();
    }

    protected abstract Map<String, Double> parseTable();
}
