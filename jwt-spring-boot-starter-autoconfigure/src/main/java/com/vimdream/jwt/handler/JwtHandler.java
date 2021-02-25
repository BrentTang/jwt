package com.vimdream.jwt.handler;

import com.alibaba.fastjson.JSONObject;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.vimdream.htool.string.StringUtil;
import com.vimdream.jwt.config.JwtConfiguration;
import com.vimdream.jwt.entity.JwtInfo;
import com.vimdream.jwt.exception.JWTException;
import com.vimdream.jwt.interceptor.JwtInterceptor;
import com.vimdream.jwt.interceptor.JwtInterceptorChain;
import com.vimdream.jwt.properties.JwtProperties;
import com.vimdream.jwt.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Title: JwtHandler
 * @Author vimdream
 * @ProjectName jwt-spring-boot-starter-autoconfigure
 * @Description: TODO
 * @Date 2020/6/23 16:47
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtHandler {

    private JwtProperties jwtProperties;

    private JwtInterceptorChain jwtInterceptorChain;

    private static final String ACCESS_TOKEN = "access_token";
    private static final String REFRESH_TOKEN = "refresh_token";

    /**
     * 创建指定过期时间的token
     * 带有token的类型
     * @param customInfo
     * @param expire
     * @return
     */
    public String generateToken(Object customInfo, Long expire, Integer tokenType) {
        try {
            String custom = null;
            if ((tokenType == null || tokenType == JwtInfo.NORMAL_TOKEN) && !jwtInterceptorChain.beforeGenerateToken(customInfo)) {
                return null;
            }
            if (customInfo instanceof String) {
                custom = (String) customInfo;
            } else {
                custom = JSONObject.toJSONString(customInfo, JwtInfo.serializeConfig);
            }
            JwtInfo jwtInfo = new JwtInfo(custom, tokenType);
            String token = JwtUtil.generateToken(jwtInfo, jwtProperties.getPrivateKey(), expire != null ? expire : jwtProperties.getExpire());
            if (!jwtInterceptorChain.afterGenerateToken(customInfo, token)) {
                return null;
            }
            return token;
        } catch (Exception e) {
            throw new JWTException("数据序列化失败");
        }
    }

    /**
     * 创建默认过期时间的token
     * @param customInfo
     * @return
     */
    public String generateToken(Object customInfo) {
        return generateToken(customInfo, jwtProperties.getExpire(), JwtInfo.NORMAL_TOKEN);
    }

    /**
     * 创建token并带有刷新token
     * @param customInfo
     * @param expire
     * @return
     */
    public Map<String, String> generateTokenWithRefreshToken(Object customInfo, Long expire) {
        Map<String, String> tokens = new HashMap<>();
        tokens.put(ACCESS_TOKEN, generateToken(customInfo, expire, null));
        tokens.put(REFRESH_TOKEN, generateToken(customInfo, getRefreshExpire(jwtProperties.getExpire()), JwtInfo.REFRESH_TOKEN));
        return tokens;
    }

    /**
     * 使用默认的过期时间
     * @param customInfo
     * @return
     */
    public Map<String, String> generateTokenWithRefreshToken(Object customInfo) {
        return generateTokenWithRefreshToken(customInfo, null);
    }

    /**
     * 刷新token
     * @param refreshToken
     * @return
     */
    public Map<String, String> refreshToken(String refreshToken) {
        return refreshToken(refreshToken, null);
    }

    /**
     * 刷新token
     * @param refreshToken
     * @param expire
     * @return
     */
    public Map<String, String> refreshToken(String refreshToken, Long expire) {
        JwtInfo jwtInfo = JwtUtil.getInfoFromToken(refreshToken, jwtProperties.getPublicKey(), JwtInfo.class);
        if (jwtInfo != null && jwtInfo.getType() == JwtInfo.REFRESH_TOKEN) {
            if (!jwtInterceptorChain.beforeRefreshToken(refreshToken, jwtInfo.getCustomInfo())) {
                return Collections.EMPTY_MAP;
            }
            Map<String, String> tokenWithRefreshToken = generateTokenWithRefreshToken(jwtInfo.getCustomInfo(), expire);
            if (!jwtInterceptorChain.afterRefreshToken(refreshToken, tokenWithRefreshToken)) {
                return Collections.EMPTY_MAP;
            }
            return tokenWithRefreshToken;
        } else
            throw new JWTException("无效的refreshToken");
    }

    /**
     * 如果token过期时间大于刷新token的过期时间, 那么刷新token时间为 expire+refreshExpire
     * @param expire
     * @return
     */
    public long getRefreshExpire(Long expire) {
        Long refreshExpire = jwtProperties.getRefreshExpire();
        return refreshExpire < expire ? (expire + refreshExpire) : refreshExpire;
    }

    /**
     * 从请求头中获取token
     * @param request
     * @return
     */
    public String selectToken(HttpServletRequest request) {
        if (request == null)
            throw new JWTException("无效的request");
        String token = request.getHeader(jwtProperties.getHeaderName());
        if (StringUtil.isBlank(token)) {
            throw new JWTException("无效的request");
        }
        return token;
    }

    public <T> T parseToken(HttpServletRequest request, Class<T> clazz) {
        return parseToken(selectToken(request), clazz);
    }

    public <T> T parseToken(String token, Class<T> clazz) {
        if (StringUtil.isBlank(token)) {
            throw new JWTException("未发现token");
        }

        if (!jwtInterceptorChain.beforeParseToken(token)) {
            return null;
        }

        JwtInfo jwtInfo = JwtUtil.getInfoFromToken(token, jwtProperties.getPublicKey(), JwtInfo.class);
        T payload = null;
        if (jwtInfo != null && StringUtil.isNotBlank(jwtInfo.getCustomInfo())) {
            try {
                payload = JSONObject.parseObject(jwtInfo.getCustomInfo(), clazz);
            } catch (Exception e) {
                payload = StringUtil.convert(jwtInfo.getCustomInfo(), clazz);
            }
        }
        if (!jwtInterceptorChain.afterParseToken(token, payload)) {
            return null;
        }
        return payload;
    }
}
