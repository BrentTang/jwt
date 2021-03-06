package com.vimdream.jwt.interceptor;

/**
 * @Title: JwtAuthenticateInterceptor
 * @Author vimdream
 * @ProjectName jwt
 * @Date 2021/2/25 14:36
 */
public interface JwtAuthenticateInterceptor {

    /**
     * 认证前拦截
     * @param token
     * @param payload token数据
     * @return true 继续执行  false 中断执行
     */
    boolean beforeAuthenticate(String token, Object payload);

    /**
     * 拦截器顺序
     * @return 值越小优先权越高
     */
    int order();

}
