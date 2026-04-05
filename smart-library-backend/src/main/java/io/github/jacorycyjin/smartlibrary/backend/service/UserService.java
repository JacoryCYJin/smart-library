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

    /**
     * 更新用户信息
     * 
     * @param userDTO 用户信息
     * @return 是否更新成功
     */
    Boolean updateUser(UserDTO userDTO);

    /**
     * 统计用户的评论数量
     * 
     * @param userId 用户ID
     * @return 评论数量
     */
    Integer countUserComments(String userId);

    /**
     * 修改密码
     * 
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    Boolean changePassword(String userId, String oldPassword, String newPassword);

    /**
     * 修改手机号
     * 
     * @param userId 用户ID
     * @param oldPhone 旧手机号
     * @param newPhone 新手机号
     * @param password 密码确认
     * @return 是否修改成功
     */
    Boolean changePhone(String userId, String oldPhone, String newPhone, String password);

    /**
     * 修改邮箱
     * 
     * @param userId 用户ID
     * @param oldEmail 旧邮箱
     * @param newEmail 新邮箱
     * @param password 密码确认
     * @return 是否修改成功
     */
    Boolean changeEmail(String userId, String oldEmail, String newEmail, String password);
}
