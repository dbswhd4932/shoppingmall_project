package com.project.shop.global.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  특정 메서드에 적용할 수 있는 AOP 메서드 생성
 *  용도 : 메서드 실행시간 측정
 */
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface TimerAop {
}
