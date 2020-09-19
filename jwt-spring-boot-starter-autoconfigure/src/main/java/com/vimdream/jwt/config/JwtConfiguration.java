package com.vimdream.jwt.config;

import com.vimdream.jwt.aspect.Authenticate;
import com.vimdream.jwt.aspect.EntityAutowired;
import com.vimdream.jwt.controller.JwtController;
import com.vimdream.jwt.handler.JwtHandler;
import com.vimdream.jwt.properties.JwtProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

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
@ConditionalOnProperty(prefix = "vimdream.jwt", name = "enabled", havingValue = "true")
public class JwtConfiguration {

    private JwtProperties jwtProperties;

    public JwtConfiguration(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Bean(name = "jwtHandler")
    public JwtHandler jwtHandler() {
        return new JwtHandler(jwtProperties);
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
    @ConditionalOnProperty(prefix = "vimdream.jwt", name = "enabled-service", havingValue = "true")
    public JwtController jwtController() {
        return new JwtController();
    }
}
