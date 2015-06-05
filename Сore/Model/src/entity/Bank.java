package entity;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by User on 14.05.2015.
 */

@XmlRootElement(name = "bank")
@XmlAccessorType(XmlAccessType.NONE)
public class Bank {

    @XmlElement
    private String name;

    @XmlElement
    private Integer wqe;

    @XmlAttribute(name = "id")
    private String ID;

    @XmlElement
    private String URL;

    @XmlElement
    private String storage;

    @XmlJavaTypeAdapter(MapAdapter.class)
    @XmlElement
    private HashMap<String, Object> loadContext;

    public static class MapAdapter extends XmlAdapter<MapType, HashMap<String, Object>> {

        @Override
        public HashMap<String, Object> unmarshal(MapType v) throws Exception {
            HashMap<String, Object> result = new HashMap<>();
            for (MapEntry entry : v.mapEntryList) {
                result.put(entry.key, entry.value);
            }
            return result;
        }

        @Override
        public MapType marshal(HashMap<String, Object> v) throws Exception {
            MapType result = new MapType();
            for (Map.Entry<String, Object> entry : v.entrySet())
                result.mapEntryList.add(new MapEntry(entry.getKey(), entry.getValue()));
            return result;
        }
    }

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

    public void setName(String name) {
        this.name = name;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void setLoadContext(HashMap<String, Object> loadContext) {
        this.loadContext = loadContext;
    }

    public String getStorage() {
        return storage;
    }

    public void setStorage(String storage) {
        this.storage = storage;
    }

    @XmlRootElement(name = "loadProperties")
    public static class MapType {

        @XmlElement(name = "property")
        public List<MapEntry> mapEntryList = new ArrayList<>();
    }

    public static class MapEntry {

        @XmlAttribute
        public String key;

        @XmlElement
        public Object value;

        public MapEntry(String key, Object value) {
            this.key = key;
            this.value = value;
        }
        public MapEntry(){}
    }

}