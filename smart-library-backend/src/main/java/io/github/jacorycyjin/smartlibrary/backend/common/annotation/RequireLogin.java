package io.github.jacorycyjin.smartlibrary.backend.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 需要登录注解
 * 在需要登录的 Controller 方法上添加此注解
 * 
 * @author Jacory
 * @date 2026/03/03
 */
@Target(ElementType.METHOD)  // 只能用在方法上
@Retention(RetentionPolicy.RUNTIME)  // 运行时生效
public @interface RequireLogin {
}
