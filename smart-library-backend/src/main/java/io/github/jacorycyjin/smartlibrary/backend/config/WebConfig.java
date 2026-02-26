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
    
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**")
                // 排除不需要认证的路径
                .excludePathPatterns(
                        "/user/login",
                        "/user/register",
                        "/book/**",
                        "/literature/**",
                        "/category/**",
                        "/tag/**",
                        "/author/**",
                        "/comment/list"  // 评论列表不需要登录
                        // /comment/create 和 /comment/{id} 需要登录
                );
    }
}
