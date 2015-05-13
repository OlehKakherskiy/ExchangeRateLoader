import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

class JDOMConfigurator extends XMLConfigurator {
    
    private Properties properties;
     
    protected JDOMConfigurator(Properties p){
	super();
	properties = p;
	
    }
    
    protected JDOMConfigurator(String s, Properties p) {
	super(s);
	properties = p;
	
    }
    
    @Override
    protected boolean load(){
	if (!super.load())
	    return false;
	try {
	    SAXBuilder parser = new SAXBuilder();
	    Document result = parser.build(configFile);
	    parseElement(result.getRootElement(), new StringBuilder());
	    } 
	catch (JDOMException e) {
	    errorMessage = e.getMessage();
	    return false;
	    } 
	catch (IOException e) {
	    errorMessage = e.getMessage();
	    return false;
	    }
	return true;
    }
    
   
    @Override
    public boolean save(){
	try{
	    String rootElement = configFile.getName().substring(0, configFile.getName().indexOf("."));
	    Document xmlDoc = buildDocument(chooseConfigures(rootElement), rootElement);
	    XMLOutputter output = new XMLOutputter();
	    output.setFormat(Format.getCompactFormat());
	    FileWriter writer;
	    writer = new FileWriter(configFile);
	    output.output(xmlDoc,writer);
	}catch(IOException e){
	    errorMessage = e.toString();
	    return false;
	}
	return true;
    }
    
    protected void setProperties(Properties p){
	properties = p;
    }
    
    // проходим от элемента xml к его children-у и создаем пару: путь(ключ) - значение
    private void parseElement(Element e, StringBuilder path){
	if(e.getChildren().size() != 0){
	    path.append(e.getName()+".");
	    List<Element> list = e.getChildren();
	    for(Element element:list)
		parseElement(element, new StringBuilder(path.toString()));
	}
	else{
	    for(Attribute a: e.getAttributes())
		properties.put(path + a.getParent().getName() + "#"+a.getName(), a.getValue());
	    if(!e.getValue().equals(""))
		properties.put(path+e.getName(), e.getValue());
	    }
	}
    
    //строим документ JDOM для записи конфигураций
    private Document buildDocument(Set<Object> keySet, String rootElementName){
	Map<String, Element> tagsMap = new HashMap<>();
	StringTokenizer tokenizer;
	StringBuilder lastPath;
	String token;
	for(Object current : keySet){
	    token = new String();
	    lastPath = new StringBuilder();
	    tokenizer = new StringTokenizer(current.toString(),".#");
	    int n = tokenizer.countTokens();
	    for(int i = 0; i < n - 1 ;i++){
		token = tokenizer.nextToken();
		if(tagsMap.containsKey(lastPath + token) == false)
		    tagsMap.put(lastPath + token.toString(), new Element(token));
		token = lastPath + token;
		if(lastPath.length() != 0 &&  tagsMap.get(token).getParent() == null && token.contains(lastPath.substring(0, lastPath.length() - 1))){
		    tagsMap.get(lastPath.substring(0, lastPath.length() - 1)).addContent(tagsMap.get(token));
		}
		lastPath.replace(0, lastPath.length(), token+".");
		}
	    if(current.toString().contains("#")){
		lastPath.setCharAt(lastPath.lastIndexOf("."),'#');
		token = tokenizer.nextToken();
		tagsMap.get(lastPath.substring(0, lastPath.length() - 1)).setAttribute(new Attribute(token, properties.getProperty(lastPath + token)));
	    }
	    else{
		token = tokenizer.nextToken();
		Element lastElement = new Element(token);
		lastElement.addContent(properties.getProperty(lastPath + token));
		tagsMap.put(lastPath + token.toString(), lastElement);
		tagsMap.get(lastPath.substring(0, lastPath.length() - 1)).addContent(lastElement);
	    }
	}
	return new Document().setRootElement(tagsMap.get(rootElementName));
	}
    
    // выбираем из Properties нужные ключи для записи в файл: отдельно - GUI, отдельно - mysql...
    private Set<Object> chooseConfigures(String param){
	HashSet<Object> configures = new HashSet<>();
	for(Object key : properties.keySet())
	    if(key.toString().contains(param))
		configures.add(key);
	return configures;
	
    }
}
