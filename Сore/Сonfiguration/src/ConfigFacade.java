import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Scanner;

public class ConfigFacade {
    
    private static final Properties properties;
    
    private static final ConfigFacade facade;
    
    static{
	properties = new Properties();
	facade = new ConfigFacade(System.getenv("PROJECT_HOME"));
    }
    
    private AbstractConfigurator configurator;
    
    private JDOMConfigurator jdomConfig;
    
    private ArrayList<File> configList = new ArrayList<>();
    
    public static Properties getProperties() {
        return properties;
    }
    
    public static ConfigFacade getInstance(){
	return facade;
    }
    
    public void storeConfigures(){
	configurator.saveConfigure();
    }
    
    private ConfigFacade(String filePath){
	jdomConfig = new JDOMConfigurator(properties);
	configurator = new AbstractConfigurator();
	configurator.configurator = jdomConfig;
	updateConfigureFiles(filePath);
	for(File f: configList){
	    loadConfigures(f);
	}
	
    }
    
    private void updateConfigureFiles(String mainFile){
	Scanner scanner = null;
	try {
	    scanner = new Scanner(new File(mainFile));
	    while(scanner.hasNext())
		configList.add(new File(scanner.nextLine()));
	}catch (FileNotFoundException e) {
	    System.out.println("Ошибка. Файл не найден.");
	}finally{
	    scanner.close();
	}
    }

    private boolean loadConfigures(File f){
	jdomConfig.configFile = f;
	return configurator.loadConfigure();
    }
    
}
