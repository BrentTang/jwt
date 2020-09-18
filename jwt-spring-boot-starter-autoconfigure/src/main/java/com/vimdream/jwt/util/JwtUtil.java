package com.vimdream.jwt.util;

import com.vimdream.htool.string.StringUtil;
import com.vimdream.htool.time.DateUtil;
import com.vimdream.jwt.entity.TokenInfo;
import com.vimdream.jwt.exception.JWTException;
import io.jsonwebtoken.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.temporal.ChronoUnit;

public class JwtUtil {

    /**
     * 私钥加密token
     *
     * @param tokenInfo      载荷中的数据
     * @param privateKey    私钥
     * @param expireSeconds 过期时间，单位秒
     * @return
     * @throws Exception
     */
    public static String generateToken(TokenInfo tokenInfo, PrivateKey privateKey, long expireSeconds) {
        return tokenInfo.builderToken(Jwts.builder())
                .setExpiration(DateUtil.plus(expireSeconds, ChronoUnit.SECONDS))
                .signWith(SignatureAlgorithm.RS256, privateKey)
                .compact();
    }

    /**
     * 公钥解析token
     *
     * @param token     用户请求中的token
     * @param publicKey 公钥
     * @return
     * @throws Exception
     */
    private static Jws<Claims> parserToken(String token, PublicKey publicKey) {
        try {
            return Jwts.parser().setSigningKey(publicKey).parseClaimsJws(token);
        } catch (SignatureException e) {
            throw new JWTException("无效的token");
        }
    }

    /**
     *  获取token中的用户信息
     * @param token   用户请求中的令牌
     * @param publicKey     公钥
     * @param infoClass     用户信息
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T getInfoFromToken(String token, PublicKey publicKey, Class<T> infoClass) {
        try {
            Jws<Claims> claimsJws = parserToken(token, publicKey);
            Claims body = claimsJws.getBody();

            Method deserialize = infoClass.getMethod("deserialize", Claims.class);
            return (T) deserialize.invoke(infoClass, body);
        } catch (NoSuchMethodException e) {
            throw new JWTException("确保" + infoClass.getName() + "存在public static T deserialize(Claims body)");
        } catch (Exception e) {
            throw new JWTException("无效的token");
        }
    }

    /**
     * 从请求中解析token
     * @param request
     * @param headerName
     * @param publicKey
     * @param infoClass
     * @param <T>
     * @return
     */
    public static <T> T getInfoFromToken(HttpServletRequest request, String headerName, PublicKey publicKey, Class<T> infoClass) {
        if (request == null) return null;

        String token = request.getHeader(headerName);
        if (StringUtil.isBlank(token)) {
            throw new JWTException("未发现token");
        }
        return JwtUtil.getInfoFromToken(token, publicKey, infoClass);
    }
}