package com.vimdream.jwt.aspect;

import com.vimdream.htool.reflect.JoinPointUtil;
import com.vimdream.htool.string.StringUtil;
import com.vimdream.jwt.annotation.JwtEntityAutowired;
import com.vimdream.jwt.exception.JWTException;
import com.vimdream.jwt.handler.JwtHandler;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @Title: EntityAutowired
 * @Author vimdream
 * @ProjectName jwt-spring-boot-starter-autoconfigure
 * @Date 2020/9/9 15:16
 */
@Aspect
@Slf4j
public class EntityAutowired {

    @Autowired
    private JwtHandler jwtHandler;

    @Pointcut(value = "@annotation(com.vimdream.jwt.annotation.JwtEntityAutowired)")
    public void pointcut(){}

    @Around("EntityAutowired.pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        JwtEntityAutowired anno = JoinPointUtil.getMethodAnno(proceedingJoinPoint, JwtEntityAutowired.class);
        boolean required = anno.required();
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null)
            throw new JWTException("无效的request");

        Object entity = null;
        try {
            entity = jwtHandler.parseToken(attributes.getRequest(), anno.entity());
        } catch (Throwable e) {
            if (StringUtil.isNotBlank(e.getMessage())) {
                log.info(e.getMessage());
            }
        }

        if (required && entity == null) {
            throw new JWTException("请确保token有效");
        } else {
            return Authenticate.execMethod(proceedingJoinPoint, true, anno.name(), anno.entity(), entity);
        }
    }
}
