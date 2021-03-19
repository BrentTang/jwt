package com.vimdream.jwt.aspect;

import com.vimdream.htool.collection.CollectionUtil;
import com.vimdream.htool.reflect.JoinPointUtil;
import com.vimdream.htool.reflect.ReflectSupport;
import com.vimdream.htool.string.StringUtil;
import com.vimdream.jwt.annotation.JwtExclude;
import com.vimdream.jwt.annotation.JwtRequestAuthority;
import com.vimdream.jwt.entity.CustomAuthority;
import com.vimdream.jwt.entity.JwtAttribute;
import com.vimdream.jwt.entity.JwtRequestAuthorityProps;
import com.vimdream.jwt.entity.ResourceAuthority;
import com.vimdream.jwt.exception.JWTException;
import com.vimdream.jwt.exception.JWTExecuteException;
import com.vimdream.jwt.handler.JwtHandler;
import com.vimdream.jwt.interceptor.JwtAuthenticateInterceptorChain;
import com.vimdream.jwt.interceptor.JwtInterceptor;
import com.vimdream.jwt.util.JwtConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Title: Authenticate
 * @Author vimdream
 * @ProjectName jwt-spring-boot-starter-autoconfigure
 * @Date 2020/6/722:47
 */
@Aspect
@Slf4j
public class Authenticate {

    @Autowired
    private JwtHandler jwtHandler;
    @Autowired
    private JwtAuthenticateInterceptorChain jwtAuthenticateInterceptorChain;
    @Autowired(required = false)
    private ResourceAuthority resourceAuthority;

    /**
     * 参数索引缓存
     */
    protected static ConcurrentHashMap<String, Integer> argIndexCache = new ConcurrentHashMap<>();

    /**
     * @annotation
     * @within
     */
    @Pointcut(value = "@annotation(com.vimdream.jwt.annotation.JwtRequestAuthority) || @within(com.vimdream.jwt.annotation.JwtRequestAuthority)")
    public void pointcut(){}

    @Around("Authenticate.pointcut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {

        JwtExclude jwtExclude = JoinPointUtil.getMethodAnno(proceedingJoinPoint, JwtExclude.class);
        if (jwtExclude != null) {
            return proceedingJoinPoint.proceed();
        }

        // 获取类上的注解
        JwtRequestAuthority classAnno = JoinPointUtil.getClassAnno(proceedingJoinPoint, JwtRequestAuthority.class);
        // 获取方法上的注解
        JwtRequestAuthority methodAnno = JoinPointUtil.getMethodAnno(proceedingJoinPoint, JwtRequestAuthority.class);

        JwtRequestAuthorityProps props = JwtRequestAuthorityProps.merge(classAnno, methodAnno);

        // 不需要token
        if (classAnno == null && methodAnno == null) {
            return proceedingJoinPoint.proceed();
        }

        if (Void.class.equals(props.getEntity())) {
            throw new JWTExecuteException("请指定注解属性entity");
        }

        // 解析token 获取用户权限
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null)
            throw new JWTException("无效的request");

        HttpServletRequest request = attributes.getRequest();
        String token = jwtHandler.selectToken(request);
        Object entityArg = jwtHandler.parseToken(attributes.getRequest(), props.getEntity());

        if (!jwtAuthenticateInterceptorChain.beforeAuthenticate(token, entityArg)) {
            return null;
        }

        // 当前请求路径 对应的 权限
        if (resourceAuthority != null) {
            props.setAuthority(resourceAuthority.getAuthority(request.getRequestURI()));
        }

        // 无需权限
        if (StringUtil.isBlank(props.getAuthority())) {
            return execMethod(proceedingJoinPoint, props.isInject(), props.getName(), props.getEntity(), entityArg);
        }

        // 获取用户权限
        if (!(entityArg instanceof CustomAuthority))
            throw new JWTExecuteException("请确保" + props.getEntity().getName() + "实现CustomAuthority接口");
        CustomAuthority customAuthority = (CustomAuthority) entityArg;
        // 鉴权
        if (hasAuthority(props.getAuthority(), customAuthority.getCustomAuthority())) {
            return execMethod(proceedingJoinPoint, props.isInject(), props.getName(), props.getEntity(), entityArg);
        }
        throw new JWTException("forbidden");
    }

    protected static Object execMethod(ProceedingJoinPoint proceedingJoinPoint, boolean inject
                    , String argName, Class entityClass, Object arg) {
        // 存入Context
        JwtContext.setAttribute(new JwtAttribute(arg));

        Object[] args = null;
        if (inject) {
            // 注入
            if (StringUtil.isBlank(argName)) {
                // 类型注入
                args = getArgsByClass(proceedingJoinPoint, entityClass, arg);
            } else {
                // 名称注入
                args = getArgsByName(proceedingJoinPoint, argName, entityClass, arg);
            }
        }
        try {
            if (inject) {
                return proceedingJoinPoint.proceed(args);
            } else {
                return proceedingJoinPoint.proceed();
            }
        } catch (Throwable throwable) {
            if (throwable instanceof RuntimeException) {
                throw (RuntimeException) throwable;
            } else {
                throw new RuntimeException(proceedingJoinPoint.getSignature().getName() + "方法执行失败");
            }
        } finally {
            JwtContext.removeAttribute();
        }
    }

    protected static <T> Object[] getArgsByName(ProceedingJoinPoint proceedingJoinPoint, String argName, Class entityClass, T arg) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Class[] parameterTypes = signature.getParameterTypes();
        if (parameterTypes == null || parameterTypes.length < 1) return proceedingJoinPoint.getArgs();
        Object[] args = proceedingJoinPoint.getArgs();
        int index = getArgIndex(JoinPointUtil.getClassName(proceedingJoinPoint)
                , proceedingJoinPoint.getSignature().getName()
                , parameterTypes
                , argName);
        if (index == -1) {
            throw new JWTExecuteException(argName + " 参数不存在");
        }
        if (index < args.length) {
            if (entityClass.equals(parameterTypes[index])) {
                args[index] = arg;
            } else {
                throw new JWTExecuteException(argName + " 参数类型必须为 " + entityClass.getName() + "不能为 " + parameterTypes[index].getName());
            }
        }
        return args;
    }

    protected static int getArgIndex(String className, String methodName, Class[] mParameterTypes, String argName) {

        // 查找缓存
        StringBuilder key = new StringBuilder();
        key.append(className);
        key.append(methodName);
        if (mParameterTypes != null && mParameterTypes.length > 0) {
            for (Class type : mParameterTypes) {
                key.append(type.getName());
            }
        }
        Integer index = argIndexCache.get(key.toString());

        if (index == null) {
            index = ReflectSupport.argIndexOfMethod(className, methodName, mParameterTypes, argName);
            if (index >= 0) {
                argIndexCache.put(key.toString(), index);
            }
        }

        return index;
    }

    /**
     * 注入指定数据
     * @param proceedingJoinPoint
     * @param infoClass
     * @param arg
     * @param <T>
     * @return
     */
    protected static <T> Object[] getArgsByClass(ProceedingJoinPoint proceedingJoinPoint, Class<T> infoClass, T arg) {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Class[] parameterTypes = signature.getParameterTypes();
        if (parameterTypes == null || infoClass == null) return proceedingJoinPoint.getArgs();
        Object[] args = proceedingJoinPoint.getArgs();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i].equals(infoClass)) {
                args[i] = arg;
                return args;
            }
        }
        return args;
    }

    /**
     * 权限匹配
     * @param needAuthority  需要的权限
     * @param authority  用户权限
     * @return
     */
    protected static boolean hasAuthority(String needAuthority, String authority) {
        // 当前接口不需要权限
        if (StringUtil.isBlank(needAuthority)) return true;
        // 用户没有任何权限
        if (StringUtil.isBlank(authority)) return false;

        String[] userAuthority = authority.split(JwtConstant.AUTHORITY_SEPARATOR);
        Set<String> userAuthoritySet = Arrays.stream(userAuthority)
                .map(curAuthority -> curAuthority.trim())
                .filter(auth -> StringUtil.isNotBlank(auth))
                .collect(Collectors.toSet());

        return hasAuthority(needAuthority, userAuthoritySet);
    }

    /**
     * 权限匹配
     * @param needAuthority  需要的权限  支持  "p1 | p2, p3"  =>  存在p1或p2 并且 存在p3
     * @param authority  用户权限
     * @return
     */
    protected static boolean hasAuthority(String needAuthority, Set<String> authority) {
        // 当前接口不需要权限
        if (StringUtil.isBlank(needAuthority)) return true;
        // 用户没有任何权限
        if (CollectionUtil.isEmpty(authority)) return false;

        String[] needPermission = needAuthority.split(JwtConstant.AUTHORITY_SEPARATOR);
        return Arrays.stream(needPermission)
                .filter(auth -> StringUtil.isNotBlank(auth))
                .allMatch(p -> match(p, authority));
    }

    /**
     * 检查是否符合
     * @param permission  p1 | p2 或 p1
     * @param authority  用户的权限
     * @return
     */
    protected static boolean match(String permission, Set<String> authority) {

        String need = permission.trim();
        if (need.contains(JwtConstant.AUTHORITY_LOGIC_OR)) {
            String[] subP = need.split(JwtConstant.AUTHORITY_LOGIC_OR_REGEX);
            return Arrays.stream(subP)
                    .filter(p -> StringUtil.isNotBlank(p))
                    .anyMatch(p -> authority.contains(p.trim()));
        }
        return authority.contains(need);

    }
}
