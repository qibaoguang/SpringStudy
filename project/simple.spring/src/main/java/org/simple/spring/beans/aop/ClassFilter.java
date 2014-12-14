package org.simple.spring.beans.aop;

public interface ClassFilter {

    boolean matches(Class<?> targetClass);

}
