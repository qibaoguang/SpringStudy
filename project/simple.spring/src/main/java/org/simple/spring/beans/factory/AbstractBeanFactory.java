package org.simple.spring.beans.factory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.simple.spring.beans.BeanDefinition;
import org.simple.spring.beans.BeanPostProcessor;

public class AbstractBeanFactory implements BeanFactory {

    private final Map<String, BeanDefinition> beanDefinitions = new ConcurrentHashMap<String, BeanDefinition>();
    private final List<String> beanNames = new ArrayList<String>();
    private final List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();

    public Object getBean(String name) throws Exception {
        BeanDefinition beanDefinition = beanDefinitions.get(name);
        if (beanDefinition == null) {
            throw new IllegalArgumentException("No bean named " + name
                    + " is defined!");
        }
        Object bean = beanDefinition.getBean();
        if (bean == null) {
            bean = doCreateBean(beanDefinition);
            bean = initializeBean(bean, name);
            beanDefinition.setBean(bean);
        }
        return bean;
    }

    protected Object initializeBean(Object bean, String name) throws Exception {
        for (BeanPostProcessor processor : beanPostProcessors) {
            bean = processor.postProcessorBeforeInitialization(bean, name);
        }
        for (BeanPostProcessor processor : beanPostProcessors) {
            bean = processor.postProcessorAfterInitialization(bean, name);
        }
        return bean;
    }

    public Object createBeanInstance(BeanDefinition beanDefinition)
            throws Exception {
        return beanDefinition.getClazz().newInstance();
    }

    protected Object doCreateBean(BeanDefinition beanDefinition)
            throws Exception {
        Object bean = createBeanInstance(beanDefinition);
        beanDefinition.setBean(bean);
        applyPropertyValues(bean, beanDefinition);
        return bean;
    }

    protected void applyPropertyValues(Object bean,
            BeanDefinition beanDefinition) throws Exception {

    }

    public void preInstantiateSingletons() throws Exception {
        Iterator<String> iterator = beanNames.iterator();
        while (iterator.hasNext()) {
            getBean(iterator.next());
        }
    }

    public void addBeanPostProcessor(BeanPostProcessor processor) {
        beanPostProcessors.add(processor);
    }

    public void register(String name, BeanDefinition bean) {
        beanDefinitions.put(name, bean);
        beanNames.add(name);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getBeanByType(Class<T> clazz) {
        List<T> beans = new ArrayList<T>();
        for (String bean : beanNames) {
            if (clazz.isAssignableFrom(beanDefinitions.get(bean).getClazz())) {
                beans.add((T) beanDefinitions.get(bean));
            }
        }
        return beans;
    }

}
