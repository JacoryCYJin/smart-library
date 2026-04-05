package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;

import java.util.Map;

/**
 * 管理员用户管理服务接口
 * 
 * @author Kiro
 * @date 2026/04/05
 */
public interface AdminUserService {

    /**
     * 获取用户列表
     */
    PageDTO getUserList(Map<String, Object> params);

    /**
     * 更新用户状态
     */
    void updateUserStatus(String userId, Integer status);

    /**
     * 更新用户角色
     */
    void updateUserRole(String userId, Integer role);

    /**
     * 删除用户
     */
    void deleteUser(String userId);
}
