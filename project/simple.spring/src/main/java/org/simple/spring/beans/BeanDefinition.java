package org.simple.spring.beans;

public class BeanDefinition {
    private Object bean;
    private Class<?> clazz;
    private String className;
    private PropertyValues properties = new PropertyValues();

    public PropertyValues getProperties() {
        return properties;
    }

    public void setProperties(PropertyValues properties) {
        this.properties = properties;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
        try {
            this.clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Object getBean() {
        return bean;
    }

}
