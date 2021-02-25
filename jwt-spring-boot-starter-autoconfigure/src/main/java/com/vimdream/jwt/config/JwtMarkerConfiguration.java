package com.vimdream.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Title: JwtMarkerConfiguration
 * @Author vimdream
 * @ProjectName jwt
 * @Date 2021/2/25 13:30
 */
@Configuration
public class JwtMarkerConfiguration {

    public JwtMarkerConfiguration() {}


    @Bean
    public JwtMarkerConfiguration.Marker jwtMarkerBean() {
        return new JwtMarkerConfiguration.Marker();
    }

    class Marker{
        Marker() {
        }
    }

}
