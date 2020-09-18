package com.vimdream.jwt.aspect;

import com.vimdream.jwt.entity.JwtAttribute;
import lombok.Data;

/**
 * @Title: JwtContext
 * @Author vimdream
 * @ProjectName jwt
 * @Date 2020/9/18 11:10
 */
public class JwtContext {

    private JwtContext(){}

    private static final ThreadLocal<JwtAttribute> map = new ThreadLocal<>();

    public static JwtAttribute getAttribute() {
        return map.get();
    }

    protected static void setAttribute(JwtAttribute attribute) {
        map.set(attribute);
    }

    protected static void removeAttribute() {
        map.remove();
    }
}
