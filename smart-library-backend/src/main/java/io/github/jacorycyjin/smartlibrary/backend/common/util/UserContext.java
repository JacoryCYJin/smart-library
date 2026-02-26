package io.github.jacorycyjin.smartlibrary.backend.common.util;

import io.github.jacorycyjin.smartlibrary.backend.dto.UserDTO;

/**
 * 用户上下文（ThreadLocal 存储当前登录用户）
 * 
 * @author Jacory
 * @date 2025/02/25
 */
public class UserContext {
    
    private static final ThreadLocal<UserDTO> USER_THREAD_LOCAL = new ThreadLocal<>();
    
    /**
     * 设置当前用户
     * 
     * @param user 用户DTO
     */
    public static void setCurrentUser(UserDTO user) {
        USER_THREAD_LOCAL.set(user);
    }
    
    /**
     * 获取当前用户
     * 
     * @return 用户DTO
     */
    public static UserDTO getCurrentUser() {
        return USER_THREAD_LOCAL.get();
    }
    
    /**
     * 获取当前用户ID
     * 
     * @return 用户ID
     */
    public static String getCurrentUserId() {
        UserDTO user = USER_THREAD_LOCAL.get();
        return user != null ? user.getUserId() : null;
    }
    
    /**
     * 清除当前用户
     */
    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }
}
