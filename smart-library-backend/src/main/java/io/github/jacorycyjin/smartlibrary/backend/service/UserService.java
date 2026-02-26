package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.dto.UserDTO;
import io.github.jacorycyjin.smartlibrary.backend.form.UserSearchForm;

import java.util.List;

/**
 * 用户服务接口
 * 
 * @author Jacory
 * @date 2025/12/11
 */
public interface UserService {

    /**
     * 登录
     * 
     * @param phoneOrEmail 手机号或邮箱
     * @param password 密码
     * @return 用户DTO（包含 token）
     */
    UserDTO login(String phoneOrEmail, String password);

    /**
     * 注册
     * 
     * @param phoneOrEmail 手机号或邮箱
     * @param password 密码
     * @param confirmPassword 确认密码
     * @return 是否注册成功
     */
    Boolean register(String phoneOrEmail, String password, String confirmPassword);

    /**
     * 根据用户ID查询用户
     * 
     * @param userId 用户ID
     * @return 用户DTO
     */
    UserDTO getUserById(String userId);

    /**
     * 搜索用户（支持多条件查询）
     * 
     * @param searchReq 查询条件
     * @return 用户列表
     */
    List<UserDTO> searchUser(UserSearchForm searchReq);
    
    /**
     * 退出登录（将 token 加入黑名单）
     * 
     * @param token JWT token
     * @return 是否退出成功
     */
    Boolean logout(String token);
}
