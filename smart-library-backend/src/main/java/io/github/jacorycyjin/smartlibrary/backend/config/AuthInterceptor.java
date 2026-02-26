package io.github.jacorycyjin.smartlibrary.backend.config;

import io.github.jacorycyjin.smartlibrary.backend.common.util.JwtUtil;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UserContext;
import io.github.jacorycyjin.smartlibrary.backend.dto.UserDTO;
import io.github.jacorycyjin.smartlibrary.backend.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器（验证 JWT token + Redis 黑名单 + Token 自动续期）
 * 
 * @author Jacory
 * @date 2025/02/25
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {
    
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RedisTemplate<String, Object> redisTemplate;
    
    // Token 黑名单前缀
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";
    // 响应头中返回新 Token 的 Header 名称
    private static final String NEW_TOKEN_HEADER = "X-New-Token";
    
    public AuthInterceptor(JwtUtil jwtUtil, UserService userService, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.redisTemplate = redisTemplate;
    }
    
    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) {
        // 从请求头获取 token
        String token = request.getHeader("Authorization");
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            
            // 1. 检查 token 是否在黑名单中
            String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
            Boolean isBlacklisted = redisTemplate.hasKey(blacklistKey);
            
            if (Boolean.TRUE.equals(isBlacklisted)) {
                // token 已被加入黑名单（用户已退出），不设置 UserContext
                // 让请求继续，但 UserContext 为空，业务层会检查并返回 401
                return true;
            }
            
            // 2. 验证 token
            if (jwtUtil.validateToken(token)) {
                String userId = jwtUtil.getUserIdFromToken(token);
                
                // 3. 查询用户信息（使用 Redis 缓存）并存入 ThreadLocal
                UserDTO user = userService.getUserById(userId);
                if (user != null) {
                    UserContext.setCurrentUser(user);
                    
                    // 4. 检查是否需要续期 Token
                    if (jwtUtil.shouldRefreshToken(token)) {
                        String newToken = jwtUtil.refreshToken(token);
                        if (newToken != null) {
                            // 将新 Token 放入响应头
                            response.setHeader(NEW_TOKEN_HEADER, newToken);
                        }
                    }
                }
            }
        }
        
        return true;
    }
    
    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, @Nullable Exception ex) {
        // 请求结束后清除 ThreadLocal
        UserContext.clear();
    }
}
