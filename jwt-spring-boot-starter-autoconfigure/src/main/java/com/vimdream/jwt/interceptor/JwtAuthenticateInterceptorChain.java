package com.vimdream.jwt.interceptor;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * @Title: JwtAuthenticateInterceptorChain
 * @Author vimdream
 * @ProjectName jwt
 * @Date 2021/2/25 12:52
 */
@Component
public class JwtAuthenticateInterceptorChain implements InitializingBean {

    @Autowired
    private ApplicationContext applicationContext;

    private final Set<JwtAuthenticateInterceptor> jwtAuthenticateInterceptorList = new TreeSet<>(Comparator.comparingInt(i -> i.order()));


    @Override
    public void afterPropertiesSet() throws Exception {
        Map<String, JwtAuthenticateInterceptor> beansOfType = applicationContext.getBeansOfType(JwtAuthenticateInterceptor.class);
        if (!CollectionUtils.isEmpty(beansOfType)) {
            beansOfType.forEach((k, v) -> jwtAuthenticateInterceptorList.add(v));
        }
    }

    public boolean beforeAuthenticate(String token, Object payload) {
        for (JwtAuthenticateInterceptor interceptor : jwtAuthenticateInterceptorList) {
            if (!interceptor.beforeAuthenticate(token, payload)) {
                return false;
            }
        }
        return true;
    }
}
