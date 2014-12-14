package org.simple.spring.beans;

import java.util.HashMap;
import java.util.Map;

import org.simple.spring.beans.io.ResourceLoader;

public abstract class AbstractBeanDefinitionReader implements
        BeanDefinitionReader {

    protected Map<String, BeanDefinition> register;
    protected ResourceLoader resourceLoader;

    public AbstractBeanDefinitionReader(ResourceLoader resourceLoader) {
        this.register = new HashMap<String, BeanDefinition>();
        this.resourceLoader = resourceLoader;
    }

    public Map<String, BeanDefinition> getRegister() {
        return register;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
