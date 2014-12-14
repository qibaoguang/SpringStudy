package org.simple.spring.beans.aop;

import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class Cglib2AopProxy extends AbstractAopProxy {

    public Cglib2AopProxy(AdvisedSupport advisedSupport) {
        super(advisedSupport);
    }

    public Object getProxy() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(advised.getTargetSource().getTargetClass());
        enhancer.setInterfaces(advised.getTargetSource().getInterfaces());
        enhancer.setCallback(new DynamicAdvisedInterceptor(advised));
        return enhancer.create();
    }

    private static class DynamicAdvisedInterceptor implements MethodInterceptor {

        private AdvisedSupport advised;
        private org.aopalliance.intercept.MethodInterceptor delegateMethodInterceptor;

        private DynamicAdvisedInterceptor(AdvisedSupport advised) {
            this.advised = advised;
            this.delegateMethodInterceptor = advised.getMethodInterceptor();
        }

        public Object intercept(Object target, Method method, Object[] args,
                MethodProxy proxy) throws Throwable {
            if (advised.getMethodMatcher() == null
                    || advised.getMethodMatcher().matches(method,
                            advised.getTargetSource().getTargetClass())) {
                return delegateMethodInterceptor
                        .invoke(new CglibMethodInvocation(advised
                                .getTargetSource().getTarget(), method, args,
                                proxy));
            }
            return new CglibMethodInvocation(advised.getTargetSource()
                    .getTarget(), method, args, proxy);
        }
    }

    private static class CglibMethodInvocation extends
            ReflectiveMethodInvocation {

        private final MethodProxy methodProxy;

        public CglibMethodInvocation(Object target, Method method,
                Object[] arguments, MethodProxy methodProxy) {
            super(target, method, arguments);
            this.methodProxy = methodProxy;
        }

        @Override
        public Object proceed() throws Throwable {
            return this.methodProxy.invoke(this.target, this.arguments);
        }
    }

}
