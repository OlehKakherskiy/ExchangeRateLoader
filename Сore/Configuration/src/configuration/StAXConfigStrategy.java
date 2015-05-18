package configuration;

import exception.ConfigException;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 * Created by User on 13.05.2015.
 */
class StAXConfigStrategy implements ConfigStrategyImpl {


    private Stack<HashMap<String,Object>> mapStack;

    private Stack<String> tagStack;

    private boolean prevClosedTag;

    private boolean wasRemoved;

    @Override
    public Map<String, Object> loadConfigs(File f) throws ConfigException {
        mapStack = new Stack<>();
        tagStack = new Stack<>();
        prevClosedTag = false;
        HashMap<String,Object> result = new HashMap<>();
        mapStack.push(result);
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        try {
            XMLStreamReader reader = inputFactory.createXMLStreamReader(new FileInputStream(f));
            while (reader.hasNext())
                if (reader.next() == XMLStreamConstants.START_ELEMENT) {
                    tagStack.push(reader.getLocalName());
                    reader.next();
                    break;
                }

            while (reader.hasNext()) {
                int code = reader.next();
                switch (code) {
                    case XMLStreamConstants.START_ELEMENT: {
                        readStartElement(reader);
                        break;
                    }
                    case XMLStreamConstants.CHARACTERS: {
                        readCharacters(reader);
                        break;
                    }
                    case XMLStreamConstants.END_ELEMENT:{
                        readEndElement(reader);
                        break;
                    }
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void storeConfigs(File f, Map<String, Object> props) throws ConfigException {
        throw new ConfigException("Операція збереження конфігураційних даних не підтримується");
    }

    private void readStartElement(XMLStreamReader reader) {
        StringBuilder tagBuilder = new StringBuilder(reader.getLocalName());
        for (int i = 0; i < reader.getAttributeCount(); i++) {
            tagBuilder.append("#" + reader.getAttributeLocalName(i) + "?" + reader.getAttributeValue(i));
        }
        tagStack.push(tagBuilder.toString());
    }

    private void readCharacters(XMLStreamReader reader) {
        String text = reader.getText().trim();
        if (text.compareTo("") == 0 && !prevClosedTag) {
            HashMap<String,Object> lastMap = new HashMap<>();
            mapStack.peek().put(tagStack.peek(), lastMap);
            mapStack.push(lastMap);
        } else {
            if(!prevClosedTag) {
                mapStack.peek().put(tagStack.pop(), text);
                wasRemoved = true;
            }
            else{
                prevClosedTag = false;
            }
        }
    }

    private void readEndElement(XMLStreamReader reader){
        if(!wasRemoved && tagStack.peek().contains(reader.getLocalName())) {
            tagStack.pop();
            mapStack.pop();
        }
        else wasRemoved =  false;
        prevClosedTag = true;
    }
}
