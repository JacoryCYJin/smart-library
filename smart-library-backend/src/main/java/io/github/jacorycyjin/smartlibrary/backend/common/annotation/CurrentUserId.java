package io.github.jacorycyjin.smartlibrary.backend.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当前用户ID注解
 * 在 Controller 方法参数上添加此注解，AOP 会自动注入当前登录用户的 ID
 * 
 * 使用示例：
 * <pre>
 * {@code
 * @PostMapping("/add")
 * @RequireLogin
 * public Result<Boolean> addFavorite(@CurrentUserId String userId, @RequestBody Form form) {
 *     // userId 会自动注入，无需手动获取
 *     return service.add(userId, form);
 * }
 * }
 * </pre>
 * 
 * @author Jacory
 * @date 2026/03/03
 */
@Target(ElementType.PARAMETER)  // 只能用在方法参数上
@Retention(RetentionPolicy.RUNTIME)  // 运行时生效
public @interface CurrentUserId {
}
