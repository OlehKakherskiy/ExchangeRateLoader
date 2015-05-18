package entity;

import javax.xml.bind.annotation.*;
import java.util.HashMap;

/**
 * Created by User on 14.05.2015.
 */

@XmlRootElement(name = "bank")
@XmlAccessorType(XmlAccessType.FIELD)
public class Bank {

    @XmlElement
    private String name;

    @XmlAttribute(name = "id")
    private String ID;

    @XmlElement
    private String URL;

    @XmlElement
    private String storage;

//    @XmlJavaTypeAdapter(MapAdapter.class)
//    private HashMap<String, Object> loadContext;

    public Bank(String name, String ID, String URL, HashMap<String, Object> loadContext) {
        this.name = name;
        this.ID = ID;
        this.URL = URL;
//        this.loadContext = loadContext;
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

//    public HashMap<String, Object> getLoadContext() {
//        return loadContext;
//    }

    public void setName(String name) {
        this.name = name;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

//    public void setLoadContext(HashMap<String, Object> loadContext) {
//        this.loadContext = loadContext;
//    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage){
        this.storage = storage;
    }
}