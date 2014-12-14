package org.simple.spring.context;

import java.util.Map;

import org.simple.spring.beans.BeanDefinition;
import org.simple.spring.beans.factory.AbstractBeanFactory;
import org.simple.spring.beans.factory.AutowireCapableBeanFactory;
import org.simple.spring.beans.io.ResourceLoader;
import org.simple.spring.beans.xml.XmlBeanDefinitionReader;

public class ClassPathXmlApplicationContext extends AbstractApplicationContext {

    private final String configLocation;

    public ClassPathXmlApplicationContext(String configLocation)
            throws Exception {
        this(configLocation, new AutowireCapableBeanFactory());
    }

    public ClassPathXmlApplicationContext(String configLocation,
            AbstractBeanFactory beanFactory) throws Exception {
        super(beanFactory);
        this.configLocation = configLocation;
        refresh();
    }

    @Override
    protected void loadBeanDefinitions(AbstractBeanFactory beanFactory)
            throws Exception {
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(
                new ResourceLoader());
        xmlBeanDefinitionReader.loadBeanDefinition(configLocation);
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : xmlBeanDefinitionReader
                .getRegister().entrySet()) {
            beanFactory.register(beanDefinitionEntry.getKey(),
                    beanDefinitionEntry.getValue());
        }
    }

}
