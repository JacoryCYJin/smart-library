package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.entity.User;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.AdminUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 管理员用户管理服务实现
 * 
 * @author Kiro
 * @date 2026/04/05
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public PageDTO<User> getUserList(Map<String, Object> params) {
        // 获取分页参数并转换类型
        Integer pageNum = 1;
        Integer pageSize = 10;
        
        if (params.get("page") != null) {
            Object pageObj = params.get("page");
            pageNum = pageObj instanceof Integer ? (Integer) pageObj : Integer.parseInt(pageObj.toString());
        }
        
        if (params.get("pageSize") != null) {
            Object pageSizeObj = params.get("pageSize");
            pageSize = pageSizeObj instanceof Integer ? (Integer) pageSizeObj : Integer.parseInt(pageSizeObj.toString());
        }
        
        // 设置分页参数
        PageHelper.startPage(pageNum, pageSize);

        // 查询用户列表
        params.put("deleted", 0);
        List<User> users = userMapper.searchUsers(params);
        PageInfo<User> pageInfo = new PageInfo<>(users);

        return new PageDTO<>(pageNum, (int) pageInfo.getTotal(), pageSize, pageInfo.getList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserStatus(String userId, Integer status) {
        User user = userMapper.findByUserId(userId);
        if (user == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "用户不存在");
        }

        user.setStatus(status);
        userMapper.updateByUserId(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserRole(String userId, Integer role) {
        User user = userMapper.findByUserId(userId);
        if (user == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "用户不存在");
        }

        user.setRole(role);
        userMapper.updateByUserId(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String userId) {
        User user = userMapper.findByUserId(userId);
        if (user == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "用户不存在");
        }

        user.setDeleted(1);
        userMapper.updateByUserId(user);
    }
}
