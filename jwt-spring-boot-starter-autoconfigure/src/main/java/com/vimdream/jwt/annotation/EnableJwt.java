package com.vimdream.jwt.annotation;

import com.vimdream.jwt.config.JwtMarkerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用Jwt
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({JwtMarkerConfiguration.class})
public @interface EnableJwt {
}
