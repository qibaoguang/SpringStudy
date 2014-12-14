package org.simple.spring.beans.aop;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.aopalliance.intercept.MethodInterceptor;

public class JdkDynamicAopProxy extends AbstractAopProxy implements
        InvocationHandler {

    public JdkDynamicAopProxy(final AdvisedSupport advised) {
        super(advised);
    }

    public Object getProxy() {
        return Proxy.newProxyInstance(getClass().getClassLoader(), advised
                .getTargetSource().getInterfaces(), this);
    }

    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        MethodInterceptor methodInterceptor = advised.getMethodInterceptor();
        if (advised.getMethodMatcher() != null
                && advised.getMethodMatcher().matches(method,
                        advised.getTargetSource().getTargetClass())) {
            return methodInterceptor.invoke(new ReflectiveMethodInvocation(
                    advised.getTargetSource().getTarget(), method, args));
        }
        return method.invoke(advised.getTargetSource().getTarget(), args);
    }
}
