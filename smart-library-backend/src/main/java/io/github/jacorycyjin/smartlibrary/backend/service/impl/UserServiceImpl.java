package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import io.github.jacorycyjin.smartlibrary.backend.entity.User;
import io.github.jacorycyjin.smartlibrary.backend.form.UserSearchForm;
import io.github.jacorycyjin.smartlibrary.backend.dto.UserDTO;
import io.github.jacorycyjin.smartlibrary.backend.mapper.UserMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.UserService;
import io.github.jacorycyjin.smartlibrary.backend.common.util.ValidationUtil;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UUIDUtil;
import io.github.jacorycyjin.smartlibrary.backend.common.util.JwtUtil;
import io.github.jacorycyjin.smartlibrary.backend.common.util.PasswordUtil;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;

import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 用户服务实现
 * 
 * @author Jacory
 * @date 2025/12/11
 */
@Service
public class UserServiceImpl implements UserService {
    
    @jakarta.annotation.Resource
    private UserMapper userMapper;
    
    @jakarta.annotation.Resource
    private JwtUtil jwtUtil;
    
    @jakarta.annotation.Resource
    private RedisTemplate<String, Object> redisTemplate;
    
    @jakarta.annotation.Resource
    private io.github.jacorycyjin.smartlibrary.backend.mapper.CommentMapper commentMapper;
    
    // Redis key 前缀
    private static final String USER_CACHE_PREFIX = "user:info:";
    // 用户信息缓存时间：30 分钟
    private static final long USER_CACHE_EXPIRE = 30;

    /**
     * 登录
     *
     * @param phoneOrEmail 手机号或邮箱
     * @param password 密码
     * @return 用户DTO（包含 token）
     */
    @Override
    public UserDTO login(String phoneOrEmail, String password) {
        ValidationUtil.validateNotEmpty(phoneOrEmail, "手机号 / 邮箱");
        ValidationUtil.validateNotEmpty(password, "密码");
        
        // 使用 searchUser 方法查找用户
        UserSearchForm searchForm = new UserSearchForm();
        searchForm.setPhoneOrEmail(phoneOrEmail);
        searchForm.setDeleted(0);
        searchForm.setLimit(1);

        List<UserDTO> users = searchUser(searchForm);
        if (users == null || users.isEmpty()) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "用户不存在");
        }

        UserDTO userDTO = users.get(0);
        
        // 验证密码（使用 BCrypt 验证）
        if (!PasswordUtil.matches(password, userDTO.getPassword())) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "密码错误");
        }
        
        // 生成 JWT token
        String token = jwtUtil.generateToken(userDTO.getUserId(), userDTO.getUsername());
        userDTO.setToken(token);
        
        // 清除密码字段（不返回给前端）
        userDTO.setPassword(null);
        
        return userDTO;
    }

    /**
     * 注册
     *
     * @param phoneOrEmail 手机号或邮箱
     * @param password 密码
     * @param confirmPassword 确认密码
     * @return 是否注册成功
     */
    @Override
    public Boolean register(String phoneOrEmail, String password, String confirmPassword) {
        ValidationUtil.validateNotEmpty(phoneOrEmail, "手机号 / 邮箱");
        ValidationUtil.validateNotEmpty(password, "密码");
        ValidationUtil.validateNotEmpty(confirmPassword, "确认密码");
        ValidationUtil.validatePasswordFormat(password);
        
        if (!password.equals(confirmPassword)) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "两次输入的密码不一致");
        }

        // 使用 searchUser 方法检查用户是否已存在
        UserSearchForm searchForm = new UserSearchForm();
        searchForm.setPhoneOrEmail(phoneOrEmail);
        searchForm.setDeleted(0);
        searchForm.setLimit(1);

        List<UserDTO> existingUsers = searchUser(searchForm);
        if (existingUsers != null && !existingUsers.isEmpty()) {
            if (ValidationUtil.validatePhoneOrEmailFormat(phoneOrEmail)) {
                throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "该邮箱已被注册");
            } else {
                throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "该手机号已被注册");
            }
        }
        
        String userId = UUIDUtil.generateUUID();
        
        // 判断是手机号还是邮箱
        boolean isEmail = ValidationUtil.validatePhoneOrEmailFormat(phoneOrEmail);
        
        // 加密密码
        String encodedPassword = PasswordUtil.encode(password);
        
        User user = User.builder()
                .userId(userId)
                .username("用户" + userId.substring(0, 8))
                .phone(isEmail ? null : phoneOrEmail)  // 如果是邮箱，phone 为 null
                .email(isEmail ? phoneOrEmail : null)  // 如果是手机号，email 为 null
                .password(encodedPassword)  // 存储加密后的密码
                .avatarUrl(null)
                .role(0)
                .bio(null)
                .status(0)
                .deleted(0)  // 设置 deleted 为 0（未删除）
                .ctime(LocalDateTime.now())
                .mtime(LocalDateTime.now())
                .build();
        return userMapper.insertUser(user) > 0;
    }

    /**
     * 根据用户ID查询用户（使用 Redis 缓存）
     * 
     * @param userId 用户ID
     * @return 用户DTO
     */
    @Override
    public UserDTO getUserById(String userId) {
        // 1. 先从 Redis 缓存中查询
        String cacheKey = USER_CACHE_PREFIX + userId;
        UserDTO cachedUser = (UserDTO) redisTemplate.opsForValue().get(cacheKey);
        
        if (cachedUser != null) {
            // 缓存命中，直接返回
            return cachedUser;
        }
        
        // 2. 缓存未命中，从数据库查询
        UserSearchForm searchForm = new UserSearchForm();
        searchForm.setUserId(userId);
        searchForm.setDeleted(0);
        searchForm.setLimit(1);
        
        List<UserDTO> users = searchUser(searchForm);
        UserDTO user = (users != null && !users.isEmpty()) ? users.get(0) : null;
        
        // 3. 将查询结果存入 Redis 缓存（30 分钟过期）
        if (user != null) {
            redisTemplate.opsForValue().set(cacheKey, user, USER_CACHE_EXPIRE, TimeUnit.MINUTES);
        }
        
        return user;
    }

    /**
     * 搜索用户（支持多条件查询）
     *
     * @param searchForm 查询条件
     * @return 用户列表
     */
    @Override
    public List<UserDTO> searchUser(UserSearchForm searchForm) {
        Map<String, Object> params = new HashMap<>();

        // 处理 phoneOrEmail 参数
        if (searchForm.getPhoneOrEmail() != null && !searchForm.getPhoneOrEmail().isEmpty()) {
            if (ValidationUtil.validatePhoneOrEmailFormat(searchForm.getPhoneOrEmail())) {
                params.put("email", searchForm.getPhoneOrEmail());
            } else {
                params.put("phone", searchForm.getPhoneOrEmail());
            }
        }

        // 添加其他查询参数
        if (searchForm.getUserId() != null && !searchForm.getUserId().isEmpty()) {
            params.put("userId", searchForm.getUserId());
        }
        if (searchForm.getUsername() != null && !searchForm.getUsername().isEmpty()) {
            params.put("username", searchForm.getUsername());
        }
        if (searchForm.getPhone() != null && !searchForm.getPhone().isEmpty()) {
            params.put("phone", searchForm.getPhone());
        }
        if (searchForm.getEmail() != null && !searchForm.getEmail().isEmpty()) {
            params.put("email", searchForm.getEmail());
        }
        if (searchForm.getRole() != null) {
            params.put("role", searchForm.getRole());
        }
        if (searchForm.getStatus() != null) {
            params.put("status", searchForm.getStatus());
        }
        if (searchForm.getDeleted() != null) {
            params.put("deleted", searchForm.getDeleted());
        } else {
            // 默认只查询未删除的用户
            params.put("deleted", 0);
        }
        if (searchForm.getLimit() != null && searchForm.getLimit() > 0) {
            params.put("limit", searchForm.getLimit());
        }

        List<User> users = userMapper.searchUser(params);
        return users.stream()
                .map(UserDTO::fromEntity)
                .collect(Collectors.toList());
    }
    
    /**
     * 退出登录（将 token 加入黑名单）
     * 
     * @param token JWT token
     * @return 是否退出成功
     */
    @Override
    public Boolean logout(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        
        // 验证 token 是否有效
        if (!jwtUtil.validateToken(token)) {
            return false;
        }
        
        // 获取 token 的剩余有效期
        long expireTime = jwtUtil.getExpirationFromToken(token).getTime() - System.currentTimeMillis();
        
        if (expireTime > 0) {
            // 将 token 加入黑名单，过期时间与 token 剩余有效期一致
            String blacklistKey = "token:blacklist:" + token;
            redisTemplate.opsForValue().set(blacklistKey, "1", expireTime, TimeUnit.MILLISECONDS);
        }
        
        return true;
    }

    /**
     * 更新用户信息
     * 
     * @param userDTO 用户信息
     * @return 是否更新成功
     */
    @Override
    public Boolean updateUser(UserDTO userDTO) {
        ValidationUtil.validateNotEmpty(userDTO.getUserId(), "用户ID");
        
        // 构建更新参数
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userDTO.getUserId());
        
        if (userDTO.getUsername() != null && !userDTO.getUsername().isEmpty()) {
            params.put("username", userDTO.getUsername());
        }
        if (userDTO.getPhone() != null && !userDTO.getPhone().isEmpty()) {
            params.put("phone", userDTO.getPhone());
        }
        if (userDTO.getEmail() != null && !userDTO.getEmail().isEmpty()) {
            params.put("email", userDTO.getEmail());
        }
        if (userDTO.getAvatarUrl() != null) {
            params.put("avatarUrl", userDTO.getAvatarUrl());
        }
        if (userDTO.getBio() != null) {
            params.put("bio", userDTO.getBio());
        }
        
        params.put("mtime", LocalDateTime.now());
        
        // 更新数据库
        int result = userMapper.updateUser(params);
        
        // 清除 Redis 缓存
        if (result > 0) {
            String cacheKey = USER_CACHE_PREFIX + userDTO.getUserId();
            redisTemplate.delete(cacheKey);
        }
        
        return result > 0;
    }

    /**
     * 统计用户的评论数量
     * 
     * @param userId 用户ID
     * @return 评论数量
     */
    @Override
    public Integer countUserComments(String userId) {
        ValidationUtil.validateNotEmpty(userId, "用户ID");
        return commentMapper.countByUserId(userId);
    }

    /**
     * 修改密码
     * 
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    @Override
    public Boolean changePassword(String userId, String oldPassword, String newPassword) {
        ValidationUtil.validateNotEmpty(userId, "用户ID");
        ValidationUtil.validateNotEmpty(oldPassword, "旧密码");
        ValidationUtil.validateNotEmpty(newPassword, "新密码");
        ValidationUtil.validatePasswordFormat(newPassword);
        
        // 查询用户
        UserDTO user = getUserById(userId);
        if (user == null) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "用户不存在");
        }
        
        // 验证旧密码
        if (!PasswordUtil.matches(oldPassword, user.getPassword())) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "旧密码错误");
        }
        
        // 加密新密码
        String encodedPassword = PasswordUtil.encode(newPassword);
        
        // 更新密码
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("password", encodedPassword);
        params.put("mtime", LocalDateTime.now());
        
        int result = userMapper.updateUser(params);
        
        // 清除 Redis 缓存
        if (result > 0) {
            String cacheKey = USER_CACHE_PREFIX + userId;
            redisTemplate.delete(cacheKey);
        }
        
        return result > 0;
    }

    /**
     * 修改手机号
     * 
     * @param userId 用户ID
     * @param oldPhone 旧手机号
     * @param newPhone 新手机号
     * @param password 密码确认
     * @return 是否修改成功
     */
    @Override
    public Boolean changePhone(String userId, String oldPhone, String newPhone, String password) {
        ValidationUtil.validateNotEmpty(userId, "用户ID");
        ValidationUtil.validateNotEmpty(oldPhone, "当前手机号");
        ValidationUtil.validateNotEmpty(newPhone, "新手机号");
        ValidationUtil.validateNotEmpty(password, "密码");
        
        // 验证新手机号格式
        if (!newPhone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "手机号格式不正确");
        }
        
        // 查询用户
        UserDTO user = getUserById(userId);
        if (user == null) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "用户不存在");
        }
        
        // 验证旧手机号
        if (!oldPhone.equals(user.getPhone())) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "当前手机号不正确");
        }
        
        // 验证密码
        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "密码错误");
        }
        
        // 检查新手机号是否已被使用
        UserSearchForm searchForm = new UserSearchForm();
        searchForm.setPhone(newPhone);
        searchForm.setDeleted(0);
        searchForm.setLimit(1);
        List<UserDTO> existingUsers = searchUser(searchForm);
        if (existingUsers != null && !existingUsers.isEmpty()) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "该手机号已被使用");
        }
        
        // 更新手机号
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("phone", newPhone);
        params.put("mtime", LocalDateTime.now());
        
        int result = userMapper.updateUser(params);
        
        // 清除 Redis 缓存
        if (result > 0) {
            String cacheKey = USER_CACHE_PREFIX + userId;
            redisTemplate.delete(cacheKey);
        }
        
        return result > 0;
    }

    /**
     * 修改邮箱
     * 
     * @param userId 用户ID
     * @param oldEmail 旧邮箱
     * @param newEmail 新邮箱
     * @param password 密码确认
     * @return 是否修改成功
     */
    @Override
    public Boolean changeEmail(String userId, String oldEmail, String newEmail, String password) {
        ValidationUtil.validateNotEmpty(userId, "用户ID");
        ValidationUtil.validateNotEmpty(oldEmail, "当前邮箱");
        ValidationUtil.validateNotEmpty(newEmail, "新邮箱");
        ValidationUtil.validateNotEmpty(password, "密码");
        
        // 验证新邮箱格式
        if (!newEmail.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "邮箱格式不正确");
        }
        
        // 查询用户
        UserDTO user = getUserById(userId);
        if (user == null) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "用户不存在");
        }
        
        // 验证旧邮箱
        if (!oldEmail.equals(user.getEmail())) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "当前邮箱不正确");
        }
        
        // 验证密码
        if (!PasswordUtil.matches(password, user.getPassword())) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "密码错误");
        }
        
        // 检查新邮箱是否已被使用
        UserSearchForm searchForm = new UserSearchForm();
        searchForm.setEmail(newEmail);
        searchForm.setDeleted(0);
        searchForm.setLimit(1);
        List<UserDTO> existingUsers = searchUser(searchForm);
        if (existingUsers != null && !existingUsers.isEmpty()) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "该邮箱已被使用");
        }
        
        // 更新邮箱
        Map<String, Object> params = new HashMap<>();
        params.put("userId", userId);
        params.put("email", newEmail);
        params.put("mtime", LocalDateTime.now());
        
        int result = userMapper.updateUser(params);
        
        // 清除 Redis 缓存
        if (result > 0) {
            String cacheKey = USER_CACHE_PREFIX + userId;
            redisTemplate.delete(cacheKey);
        }
        
        return result > 0;
    }
}
