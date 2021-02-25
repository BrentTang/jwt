package com.vimdream.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Title: JwtServiceMarkerConfiguration
 * @Author vimdream
 * @ProjectName jwt
 * @Date 2021/2/25 13:26
 */
@Configuration
public class JwtServiceMarkerConfiguration {

    public JwtServiceMarkerConfiguration() {}


    @Bean
    public JwtServiceMarkerConfiguration.Marker jwtServiceMarkerBean() {
        return new JwtServiceMarkerConfiguration.Marker();
    }

    class Marker{
        Marker() {
        }
    }

}
