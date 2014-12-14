package org.simple.spring.beans.aop;

public interface PointcutAdvisor extends Advisor {

    Pointcut getPointcut();

}
