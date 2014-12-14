package org.simple.spring.beans.aop;

import org.simple.spring.beans.factory.BeanFactory;

public interface BeanFactoryAware {

    void setBeanFactory(BeanFactory factory) throws Exception;

}
