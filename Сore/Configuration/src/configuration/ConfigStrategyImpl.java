package configuration;

import exception.ConfigException;

import java.io.File;
import java.util.Map;

/**
 * Created by User on 13.05.2015.
 */
interface ConfigStrategyImpl {

    Map<String,Object> loadConfigs(File f) throws ConfigException;

    void storeConfigs(File f, Map<String,Object> props) throws ConfigException;
}
