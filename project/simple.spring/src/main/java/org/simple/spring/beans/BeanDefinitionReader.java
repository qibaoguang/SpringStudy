package org.simple.spring.beans;

public interface BeanDefinitionReader {

    void loadBeanDefinition(String location) throws Exception;

}
