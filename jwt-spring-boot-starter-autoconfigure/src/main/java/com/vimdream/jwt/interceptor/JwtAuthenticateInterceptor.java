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
     * @return
     */
    boolean beforeAuthenticate(String token, Object payload);

    /**
     * 拦截器顺序
     * @return 值越小优先权越高
     */
    int order();

}
