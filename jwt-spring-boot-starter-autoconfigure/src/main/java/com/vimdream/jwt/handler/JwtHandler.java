package com.vimdream.jwt.handler;

import com.alibaba.fastjson.JSONObject;
import com.vimdream.htool.string.StringUtil;
import com.vimdream.jwt.entity.JwtInfo;
import com.vimdream.jwt.exception.JWTException;
import com.vimdream.jwt.properties.JwtProperties;
import com.vimdream.jwt.util.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.servlet.http.HttpServletRequest;
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
public class JwtHandler {

    private JwtProperties jwtProperties;

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
            JwtInfo jwtInfo = new JwtInfo(JSONObject.toJSONString(customInfo), tokenType);
            return JwtUtil.generateToken(jwtInfo, jwtProperties.getPrivateKey(), expire != null ? expire : jwtProperties.getExpire());
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
        return generateToken(customInfo, null, null);
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
            return generateTokenWithRefreshToken(jwtInfo.getCustomInfo(), expire);
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

    public <T> T parseToken(HttpServletRequest request, Class<T> clazz) {
        if (request == null)
            throw new JWTException("无效的request");
        String token = request.getHeader(jwtProperties.getHeaderName());
        return parseToken(token, clazz);
    }

    public <T> T parseToken(String token, Class<T> clazz) {
        if (StringUtil.isBlank(token)) {
            throw new JWTException("未发现token");
        }
        JwtInfo jwtInfo = JwtUtil.getInfoFromToken(token, jwtProperties.getPublicKey(), JwtInfo.class);
        if (jwtInfo != null && StringUtil.isNotBlank(jwtInfo.getCustomInfo())) {
            try {
                return JSONObject.parseObject(jwtInfo.getCustomInfo(), clazz);
            } catch (Exception e) {
                return (T) StringUtil.convert(jwtInfo.getCustomInfo(), clazz);
            }
        }
        return null;
    }
}
