package org.simple.spring.beans.xml;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.simple.spring.beans.AbstractBeanDefinitionReader;
import org.simple.spring.beans.BeanDefinition;
import org.simple.spring.beans.BeanReference;
import org.simple.spring.beans.PropertyValue;
import org.simple.spring.beans.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    public XmlBeanDefinitionReader(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    public void loadBeanDefinition(String location) throws Exception {
        InputStream inputStream = getResourceLoader().getResource(location)
                .getInputStream();
        doLoadBeanDefinitions(inputStream);
    }

    protected void doLoadBeanDefinitions(InputStream inputStream)
            throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        Document doc = docBuilder.parse(inputStream);
        registerBeanDefinitions(doc);
        inputStream.close();
    }

    public void registerBeanDefinitions(Document doc) {
        Element root = doc.getDocumentElement();
        parseBeanDefinitions(root);
    }

    protected void parseBeanDefinitions(Element root) {
        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
                Element elem = (Element) node;
                processBeanDefinition(elem);
            }
        }
    }

    protected void processBeanDefinition(Element elem) {
        String id = elem.getAttribute("id");
        String className = elem.getAttribute("class");
        BeanDefinition beanDefinition = new BeanDefinition();
        processProperty(elem, beanDefinition);
        beanDefinition.setClassName(className);
        getRegister().put(id, beanDefinition);
    }

    private void processProperty(Element elem, BeanDefinition beanDefinition) {
        NodeList nodes = elem.getElementsByTagName("property");
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node instanceof Element) {
                Element propertyElem = (Element) node;
                String name = propertyElem.getAttribute("name");
                String value = propertyElem.getAttribute("value");
                if (value != null && value.length() > 0) {
                    beanDefinition.getProperties().add(
                            new PropertyValue(name, value));
                } else {
                    String ref = propertyElem.getAttribute("ref");
                    if (ref == null || ref.length() == 0) {
                        throw new IllegalArgumentException(
                                "Configuration problem: <property> element for property '"
                                        + name
                                        + "' must specify a ref or value");
                    }
                    BeanReference beanReference = new BeanReference(ref);
                    beanDefinition.getProperties().add(
                            new PropertyValue(name, beanReference));
                }
            }
        }
    }

}
