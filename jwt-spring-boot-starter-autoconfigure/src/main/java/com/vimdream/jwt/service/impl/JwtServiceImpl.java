package com.vimdream.jwt.service.impl;

import com.vimdream.jwt.handler.JwtHandler;
import com.vimdream.jwt.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

/**
 * @Title: JwtServiceImpl
 * @Author vimdream
 * @ProjectName jwt
 * @Date 2021/2/25 13:18
 */
@Service
@ConditionalOnBean(JwtHandler.class)
public class JwtServiceImpl implements JwtService {

    @Autowired
    private JwtHandler jwtHandler;



}
