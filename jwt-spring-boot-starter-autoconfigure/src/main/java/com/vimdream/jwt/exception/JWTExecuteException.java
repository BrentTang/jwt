package com.vimdream.jwt.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Title: JWTExecuteException
 * @Author vimdream
 * @ProjectName jwt-spring-boot-starter-autoconfigure
 * @Date 2020/9/9 17:22
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JWTExecuteException extends RuntimeException {

    private String message;

}
