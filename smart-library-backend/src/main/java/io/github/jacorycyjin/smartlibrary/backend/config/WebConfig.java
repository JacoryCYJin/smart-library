package io.github.jacorycyjin.smartlibrary.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置（注册拦截器）
 * 
 * @author Jacory
 * @date 2025/02/25
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    private final AuthInterceptor authInterceptor;
    
    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }
    
    /**
     * 注册拦截器
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                // 排除不需要认证的路径
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/book/search",      // 图书搜索不需要登录（游客可访问）
                        "/literature/**",
                        "/category/**",
                        "/tag/**",
                        "/author/**",
                        "/comment/list",     // 评论列表不需要登录
                        "/graph/**"          // AI 人物关系图谱不需要登录（游客可访问）
                        // /book/{bookId} 需要经过拦截器（设置 UserContext 以记录浏览历史），但不强制登录
                        // /favorite/check 需要经过拦截器（设置 UserContext），但不强制登录
                        // /comment/create 和 /comment/{id} 需要登录
                        // /favorite/add, /favorite/remove, /favorite/list, /favorite/count 需要登录
                );
    }
}
