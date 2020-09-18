package com.vimdream.jwt.entity;

import io.jsonwebtoken.JwtBuilder;

public interface TokenInfo {

    JwtBuilder builderToken(JwtBuilder builder);

}