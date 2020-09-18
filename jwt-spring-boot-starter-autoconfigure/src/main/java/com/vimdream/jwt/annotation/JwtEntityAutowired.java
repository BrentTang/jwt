package com.vimdream.jwt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JwtEntityAutowired {

    /**
     * token反序列化的实体class
     * @return
     */
    Class entity();

    /**
     * 注入的参数名  优先使用name注入
     * @return
     */
    String name() default "";

    /**
     * 是否必须
     * @return
     */
    boolean required() default true;
}
