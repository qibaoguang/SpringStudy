package org.simple.spring.beans.factory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.simple.spring.beans.BeanDefinition;
import org.simple.spring.beans.BeanReference;
import org.simple.spring.beans.PropertyValue;
import org.simple.spring.beans.aop.BeanFactoryAware;

public class AutowireCapableBeanFactory extends AbstractBeanFactory {

    @Override
    protected void applyPropertyValues(Object bean,
            BeanDefinition beanDefinition) throws Exception {
        if (bean instanceof BeanFactoryAware) {
            ((BeanFactoryAware) bean).setBeanFactory(this);
        }
        for (PropertyValue pv : beanDefinition.getProperties().getValues()) {
            Object value = pv.getValue();
            if (value instanceof BeanReference) {
                BeanReference beanReference = (BeanReference) value;
                value = getBean(beanReference.getName());
            }
            try {
                String setter = "set"
                        + pv.getName().substring(0, 1).toUpperCase()
                        + pv.getName().substring(1);
                Method method = bean.getClass().getDeclaredMethod(setter,
                        value.getClass());
                method.setAccessible(true);
                method.invoke(bean, value);
            } catch (NoSuchMethodException e) {
                Field field = bean.getClass().getDeclaredField(pv.getName());
                field.setAccessible(true);
                field.set(bean, value);
            }
        }
    }
}
