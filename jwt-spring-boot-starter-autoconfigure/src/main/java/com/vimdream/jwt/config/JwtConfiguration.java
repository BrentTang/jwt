package com.vimdream.jwt.config;

import com.vimdream.jwt.aspect.Authenticate;
import com.vimdream.jwt.aspect.EntityAutowired;
import com.vimdream.jwt.controller.JwtController;
import com.vimdream.jwt.handler.JwtHandler;
import com.vimdream.jwt.interceptor.JwtAuthenticateInterceptorChain;
import com.vimdream.jwt.interceptor.JwtInterceptorChain;
import com.vimdream.jwt.properties.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

/**
 * @Title: JwtConfiguration
 * @Author vimdream
 * @ProjectName jwt-spring-boot-starter-autoconfigure
 * @Description: TODO
 * @Date 2020/6/23 15:53
 */
@Configuration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(JwtProperties.class)
//@ConditionalOnProperty(prefix = "vimdream.jwt", name = "enabled", havingValue = "true")
@ConditionalOnBean(JwtMarkerConfiguration.Marker.class)
@Import({JwtHandler.class})
public class JwtConfiguration {

    @Autowired
    private JwtProperties jwtProperties;

//    public JwtConfiguration(JwtProperties jwtProperties) {
//        this.jwtProperties = jwtProperties;
//    }

    @Bean
    public JwtHandler jwtHandler() {
        return new JwtHandler(jwtProperties, jwtInterceptorChain());
    }

    @Bean
    public JwtInterceptorChain jwtInterceptorChain() {
        return new JwtInterceptorChain();
    }

    @Bean
    public JwtAuthenticateInterceptorChain jwtAuthenticateInterceptorChain() {
        return new JwtAuthenticateInterceptorChain();
    }

    @Bean
    public Authenticate authenticate() {
        return new Authenticate();
    }

    @Bean
    public EntityAutowired entityAutowired() {
        return new EntityAutowired();
    }

    @Bean
//    @ConditionalOnProperty(prefix = "vimdream.jwt", name = "enabled-service", havingValue = "true")
    @ConditionalOnBean(JwtServiceMarkerConfiguration.Marker.class)
    public JwtController jwtController() {
        return new JwtController();
    }
}
