package app;

import com.sun.istack.internal.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 13.05.2015.
 */
public class Context {

    private Map<String, Object> context;

    public Context(){
        context = new HashMap<>();
    }

    public Context(@NotNull HashMap<String,Object> context){
        this.context = context;
    }

    public void addValue(String key, Object value){
        context.put(key,value);
    }

    public void addMap(Map<String,Object> map){
        context.putAll(map);
    }

    public void clear(){
        context.clear();
    }
    public Object getValue(String key){
        return context.get(key);
    }
}
