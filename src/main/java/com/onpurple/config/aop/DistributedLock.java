package com.onpurple.config.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) // 컴파일 이후에도 JVM에 의해 계속 참조될 수 있음
@Target(ElementType.METHOD) // 해당 어노테이션이 메소드 선언부에만 붙을 수 있음
public @interface DistributedLock {

}