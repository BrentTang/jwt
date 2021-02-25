package com.vimdream.jwt.annotation;

import com.vimdream.jwt.config.JwtServiceMarkerConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Title: EnableJwtService
 * @Author vimdream
 * @ProjectName jwt
 * @Date 2021/2/25 13:22
 * 启用刷新token服务
 * /jwt/refresh
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Import({JwtServiceMarkerConfiguration.class})
public @interface EnableJwtService {
}
