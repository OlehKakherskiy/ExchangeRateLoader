package configuration;

import app.IActionBuilder;
import app.IActionFactory;
import app.IValidatorFactory;
import app.IViewFactory;
import entity.BankList;
import exception.ConfigException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 13.05.2015.
 */
public class ConfigFacade {

    private static ConfigFacade facade = new ConfigFacade();

    private Map<String, Object> systemProperties;

    private File sysConfigsFile;

    private IActionBuilder actionBuilder;

    public IViewFactory viewFactory;

    private IValidatorFactory validatorFactory;

    private IActionFactory actionFactory;

    private ConfigStrategyImpl configStrategy;

    private ConfigFacade() {
        systemProperties = new HashMap<>();
        loadStartConfigs();
        try {
            loadConfigs(sysConfigsFile);
        } catch (ConfigException e) {
            e.printStackTrace();
        }
    }

    public static ConfigFacade getInstance(){
        return facade;
    }

    public void loadConfigs(File f) throws ConfigException {
        systemProperties = configStrategy.loadConfigs(f);
        try {
            actionBuilder = (IActionBuilder) Class.forName((String) getSystemProperty("actionBuilder")).newInstance();
            viewFactory = (IViewFactory) Class.forName((String) getSystemProperty("viewFactory")).newInstance();
            validatorFactory = (IValidatorFactory) Class.forName((String) getSystemProperty("validatorFactory")).newInstance();
            actionFactory = (IActionFactory) Class.forName((String) getSystemProperty("actionFactory")).newInstance();
            loadBanksConfigs();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void storeConfigs(Map<String, Object> props) throws ConfigException {
        configStrategy.storeConfigs(sysConfigsFile, props);
    }

    public Object getSystemProperty(String ID){
        HashMap<String,Object> result = new HashMap<>();
        for(String key: systemProperties.keySet()){
            if(key.contains(ID))
                result.put(key,systemProperties.get(key));
        }
        if (result.size() == 1)
            return result.get(ID);
        return result;
    }

    private void loadStartConfigs() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(System.getenv("ExchangeRateLoader")));
            sysConfigsFile = new File(reader.readLine());
            configStrategy = (ConfigStrategyImpl) Class.forName(reader.readLine()).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadBanksConfigs(){
        HashMap<String,String> bindConfigs = (HashMap<String, String>) getSystemProperty("binding");
        try {
            System.out.println(Class.forName(bindConfigs.get("class")));
            JAXBContext jaxbContext = JAXBContext.newInstance(Class.forName(bindConfigs.get("class")));
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            BankList bankList = (BankList) unmarshaller.unmarshal(new File(bindConfigs.get("source")));
            systemProperties.put("bankList",bankList);
        } catch (JAXBException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public IActionBuilder getActionBuilder() {
        return actionBuilder;
    }

    public IViewFactory getViewFactory() {
        return viewFactory;
    }

    public IValidatorFactory getValidatorFactory() {
        return validatorFactory;
    }

    public IActionFactory getActionFactory() {
        return actionFactory;
    }

    public static void main(String[] args) {
        ConfigFacade f = ConfigFacade.getInstance();
        try {
            f.loadConfigs(f.sysConfigsFile);
            System.out.println(f.systemProperties);
        } catch (ConfigException e) {
            e.printStackTrace();
        }

    }
}
