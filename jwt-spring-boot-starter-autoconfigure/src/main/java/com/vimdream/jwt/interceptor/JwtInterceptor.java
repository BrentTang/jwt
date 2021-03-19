package com.vimdream.jwt.interceptor;

import java.util.Map;

/**
 * @Title: JwtInterceptor
 * @Author vimdream
 * @ProjectName jwt
 * @Date 2020/12/30 21:50
 */
public interface JwtInterceptor {

    /**
     * 生成前执行
     * @param payload
     * @return true 继续执行  false 中断执行
     */
    boolean beforeGenerateToken(Object payload);

    /**
     * 生成后执行
     * @param payload
     * @return true 继续执行  false 中断执行
     */
    boolean afterGenerateToken(Object payload, String token);

    /**
     * 解析前执行
     * 解析token与refreshToken时都会调
     * @param token
     * @return true 继续执行  false 中断执行
     */
    boolean beforeParseToken(String token);

    /**
     * 解析后执行
     * 解析token与refreshToken时都会调
     * @param token
     * @param payload
     * @return true 继续执行  false 中断执行
     */
    boolean afterParseToken(String token, Object payload);

    /**
     * 刷新前执行
     * @param refreshToken
     * @param payload
     * @return true 继续执行  false 中断执行
     */
    boolean beforeRefreshToken(String refreshToken, String payload);

    /**
     * 刷新后执行
     * @param refreshToken
     * @param newToken
     * @return true 继续执行  false 中断执行
     */
    boolean afterRefreshToken(String refreshToken, Map<String, String> newToken);

    /**
     * 拦截器顺序
     * @return 值越小优先权越高
     */
    int order();

}
