package io.github.jacorycyjin.smartlibrary.backend.common.aspect;

import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UserContext;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

/**
 * 认证切面
 * 自动检查带有 @RequireLogin 注解的方法是否已登录
 * 
 * @author Jacory
 * @date 2026/03/03
 */
@Aspect
@Component
public class AuthAspect {
    
    /**
     * 在执行带有 @RequireLogin 注解的方法之前，检查用户是否登录
     */
    @Before("@annotation(io.github.jacorycyjin.smartlibrary.backend.common.annotation.RequireLogin)")
    public void checkLogin() {
        String userId = UserContext.getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            throw new BusinessException(ApiCode.UNAUTHORIZED.getCode(), "请先登录");
        }
    }
}
