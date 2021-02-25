package com.vimdream.jwt.interceptor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * @Title: JwtInterceptorChain
 * @Author vimdream
 * @ProjectName jwt
 * @Date 2021/2/25 12:52
 */
public class JwtInterceptorChain implements InitializingBean {

    @Autowired
    private ApplicationContext applicationContext;

    private final Set<JwtInterceptor> jwtInterceptorList = new TreeSet<>(Comparator.comparingInt(i -> i.order()));


    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, JwtInterceptor> beansOfType = applicationContext.getBeansOfType(JwtInterceptor.class);
        if (!CollectionUtils.isEmpty(beansOfType)) {
            beansOfType.forEach((k, v) -> jwtInterceptorList.add(v));
        }
    }

    public boolean beforeGenerateToken(Object payload) {
        for (JwtInterceptor interceptor : jwtInterceptorList) {
            if (!interceptor.beforeGenerateToken(payload)) {
                return false;
            }
        }
        return true;
    }

    public boolean afterGenerateToken(Object payload, String token) {
        for (JwtInterceptor interceptor : jwtInterceptorList) {
            if (!interceptor.afterGenerateToken(payload, token)) {
                return false;
            }
        }
        return true;
    }

    public boolean beforeParseToken(String token) {
        for (JwtInterceptor interceptor : jwtInterceptorList) {
            if (!interceptor.beforeParseToken(token)) {
                return false;
            }
        }
        return true;
    }

    public boolean afterParseToken(String token, Object payload) {
        for (JwtInterceptor interceptor : jwtInterceptorList) {
            if (!interceptor.afterParseToken(token, payload)) {
                return false;
            }
        }
        return true;
    }

    public boolean beforeRefreshToken(String refreshToken, String payload) {
        for (JwtInterceptor interceptor : jwtInterceptorList) {
            if (!interceptor.beforeRefreshToken(refreshToken, payload)) {
                return false;
            }
        }
        return true;
    }

    public boolean afterRefreshToken(String refreshToken, Map<String, String> newToken) {
        for (JwtInterceptor interceptor : jwtInterceptorList) {
            if (!interceptor.afterRefreshToken(refreshToken, newToken)) {
                return false;
            }
        }
        return true;
    }
}
