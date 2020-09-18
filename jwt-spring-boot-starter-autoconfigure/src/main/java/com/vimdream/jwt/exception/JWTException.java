package com.vimdream.jwt.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Title: JWTException
 * @Author vimdream
 * @ProjectName jwt-spring-boot-starter-autoconfigure
 * @Description: TODO
 * @Date 2020/6/23 16:18
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class JWTException extends RuntimeException {

    private String message;

}
