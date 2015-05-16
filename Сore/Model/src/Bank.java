import java.util.HashMap;

/**
 * Created by User on 14.05.2015.
 */
public class Bank {

    private String name;

    private String ID;

    private String URL;

    private HashMap<String, Object> loadContext;

    public Bank(String name, String ID, String URL, HashMap<String, Object> loadContext) {
        this.name = name;
        this.ID = ID;
        this.URL = URL;
        this.loadContext = loadContext;
    }

    public Bank() {
    }

    public String getName() {
        return name;
    }

    public String getID() {
        return ID;
    }

    public String getURL() {
        return URL;
    }

    public HashMap<String, Object> getLoadContext() {
        return loadContext;
    }
}
