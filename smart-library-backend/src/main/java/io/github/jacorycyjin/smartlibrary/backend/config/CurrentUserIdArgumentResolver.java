package io.github.jacorycyjin.smartlibrary.backend.config;

import io.github.jacorycyjin.smartlibrary.backend.common.annotation.CurrentUserId;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UserContext;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * 当前用户ID参数解析器
 * 自动将 ThreadLocal 中的 userId 注入到带有 @CurrentUserId 注解的参数中
 * 
 * @author Jacory
 * @date 2026/03/03
 */
public class CurrentUserIdArgumentResolver implements HandlerMethodArgumentResolver {
    
    /**
     * 判断是否支持该参数
     * 只有带 @CurrentUserId 注解且类型为 String 的参数才支持
     */
    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        // 检查参数是否有 @CurrentUserId 注解
        boolean hasAnnotation = parameter.hasParameterAnnotation(CurrentUserId.class);
        // 检查参数类型是否为 String
        boolean isStringType = String.class.equals(parameter.getParameterType());
        
        return hasAnnotation && isStringType;
    }
    
    /**
     * 解析参数值
     * 从 UserContext 中获取当前登录用户的 ID
     */
    @Override
    @Nullable
    public Object resolveArgument(
            @NonNull MethodParameter parameter,
            @Nullable ModelAndViewContainer mavContainer,
            @NonNull NativeWebRequest webRequest,
            @Nullable WebDataBinderFactory binderFactory) {
        
        // 从 ThreadLocal 中获取 userId
        return UserContext.getCurrentUserId();
    }
}
