package org.simple.spring.beans.aop;

import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.simple.spring.beans.BeanPostProcessor;
import org.simple.spring.beans.factory.AbstractBeanFactory;
import org.simple.spring.beans.factory.BeanFactory;

public class AspectJAwareAdvisorAutoProxyCreator implements BeanFactoryAware,
        BeanPostProcessor {

    private AbstractBeanFactory beanFactory;

    public Object postProcessorBeforeInitialization(Object bean, String beanName)
            throws Exception {
        return bean;
    }

    public Object postProcessorAfterInitialization(Object bean, String beanName)
            throws Exception {
        if (bean instanceof AspectJExpressionPointcutAdvisor) {
            return bean;
        }
        if (bean instanceof MethodInterceptor) {
            return bean;
        }
        List<AspectJExpressionPointcutAdvisor> advisors = beanFactory
                .getBeanByType(AspectJExpressionPointcutAdvisor.class);
        for (AspectJExpressionPointcutAdvisor advisor : advisors) {
            if (advisor.getPointcut().getClassFilter().matches(bean.getClass())) {
                ProxyFactory advisedSupport = new ProxyFactory();
                advisedSupport.setMethodInterceptor((MethodInterceptor) advisor
                        .getAdvice());

                TargetSource targetSource = new TargetSource(bean,
                        bean.getClass(), bean.getClass().getInterfaces());
                advisedSupport.setTargetSource(targetSource);
                return advisedSupport.getProxy();
            }
        }
        return bean;
    }

    public void setBeanFactory(BeanFactory factory) throws Exception {
        this.beanFactory = (AbstractBeanFactory) factory;
    }

}
