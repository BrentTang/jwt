package com.vimdream.jwt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface JwtRequestAuthority {

    String authority() default "";
    /**
     * token反序列化的实体class
     * @return
     */
    Class entity() default Void.class;

    /**
     * 注入的参数名
     * @return
     */
    String name() default "";

    boolean inject() default true;
}
