package com.vimdream.jwt.controller;

import com.alibaba.fastjson.JSONObject;
import com.vimdream.jwt.exception.JWTException;
import com.vimdream.jwt.handler.JwtHandler;
import com.vimdream.jwt.interceptor.JwtInterceptorChain;
import com.vimdream.jwt.properties.JwtProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @Title: JwtController
 * @Author vimdream
 * @ProjectName jwt-spring-boot-starter
 * @Date 2020/7/8 12:34
 */
@RestController
@RequestMapping("/jwt")
public class JwtController {

    @Autowired
    private JwtHandler jwtHandler;
    @Autowired
    private JwtInterceptorChain jwtInterceptorChain;

    @GetMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestParam("refresh_token") String refreshToken) {
        JwtProperties jwtProperties = jwtHandler.getJwtProperties();
        /*if (!jwtInterceptorChain.beforeRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }*/
        Map<String, String> token = jwtHandler.refreshToken(refreshToken);
        /*if (jwtInterceptorChain.afterRefreshToken(refreshToken, token)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }*/
        if (StringUtils.isBlank(jwtProperties.getRefreshTokenTemplate())) {
            return ResponseEntity.ok(token);
        }

        String refreshTokenTemplate = jwtProperties.getRefreshTokenTemplate();
        String result = refreshTokenTemplate.replaceAll(jwtProperties.getResultPlaceholder(), JSONObject.toJSONString(token));

        return ResponseEntity.ok(JSONObject.parse(result));
    }

}
