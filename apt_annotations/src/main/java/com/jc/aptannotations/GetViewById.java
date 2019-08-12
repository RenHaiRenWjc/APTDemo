package com.jc.aptannotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * ClassName:com.jc.aptannotations
 * Description: 自定义注解模块
 * JcChen on 2019/8/11 15:11
 */
@Retention(RetentionPolicy.SOURCE)  //注解的存活时间
@Target(ElementType.FIELD) //运用注解的地方，field---运用在属性注解
public @interface GetViewById {
    int value(); //value用于接收注解该View的id值
}
