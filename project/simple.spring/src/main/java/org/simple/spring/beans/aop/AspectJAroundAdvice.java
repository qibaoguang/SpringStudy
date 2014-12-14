package org.simple.spring.beans.aop;

import java.lang.reflect.Method;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.simple.spring.beans.factory.BeanFactory;

public class AspectJAroundAdvice implements Advice, MethodInterceptor {

    private BeanFactory beanFactory;
    private Method adviceMethod;
    private String instanceName;

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public Method getAdviceMethod() {
        return adviceMethod;
    }

    public void setAdviceMethod(Method adviceMethod) {
        this.adviceMethod = adviceMethod;
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        return adviceMethod.invoke(beanFactory.getBean(instanceName),
                invocation);
    }

}
