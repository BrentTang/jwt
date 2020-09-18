package com.vimdream.jwt.entity;

import com.vimdream.htool.string.StringUtil;
import com.vimdream.jwt.annotation.JwtRequestAuthority;
import com.vimdream.jwt.aspect.Authenticate;
import com.vimdream.jwt.util.JwtConstant;
import lombok.Data;

/**
 * @Title: JwtRequestAuthorityProps
 * @Author vimdream
 * @ProjectName jwt
 * @Date 2020/9/18 10:31
 */
@Data
public class JwtRequestAuthorityProps {

    /**
     * 以逗号分割的 权限
     */
    private String authority;

    /**
     * token反序列化的实体class
     * @return
     */
    private Class entity;

    /**
     * 注入的参数名
     * @return
     */
    private String name;

    /**
     * 是否注入
     */
    private boolean inject;

    public static JwtRequestAuthorityProps merge(JwtRequestAuthority classAnno, JwtRequestAuthority methodAnno) {

        JwtRequestAuthorityProps props = new JwtRequestAuthorityProps();
        StringBuilder needAuthority = new StringBuilder();

        if (classAnno != null) {
            props.setEntity(classAnno.entity());
            props.setName(classAnno.name());
            props.setInject(classAnno.inject());
            String classAuth = classAnno.authority();
            if (classAuth.endsWith(JwtConstant.AUTHORITY_SEPARATOR)) {
                needAuthority.append(classAuth);
            } else if (StringUtil.isNotBlank(classAuth)) {
                needAuthority.append(classAuth + JwtConstant.AUTHORITY_SEPARATOR);
            }
        }

        if (methodAnno != null) {
            props.setInject(methodAnno.inject());
            Class entityClazz = methodAnno.entity();
            if (!Void.class.equals(entityClazz)) {
                props.setEntity(entityClazz);
            }
            if (StringUtil.isNotBlank(methodAnno.name())) {
                props.setName(methodAnno.name());
            }
            needAuthority.append(methodAnno.authority());
        }

        props.setAuthority(needAuthority.toString());
        return props;
    }
}
