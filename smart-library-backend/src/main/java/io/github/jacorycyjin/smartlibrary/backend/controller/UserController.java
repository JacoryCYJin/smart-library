package io.github.jacorycyjin.smartlibrary.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import io.github.jacorycyjin.smartlibrary.backend.service.UserService;
import io.github.jacorycyjin.smartlibrary.backend.dto.UserDTO;
import io.github.jacorycyjin.smartlibrary.backend.form.LoginForm;
import io.github.jacorycyjin.smartlibrary.backend.form.RegisterForm;
import io.github.jacorycyjin.smartlibrary.backend.form.UserSearchForm;
import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.util.UserContext;
import io.github.jacorycyjin.smartlibrary.backend.vo.UserPublicVO;
import io.github.jacorycyjin.smartlibrary.backend.vo.UserDetailVO;
import java.util.List;

/**
 * 用户控制器
 * 
 * @author Jacory
 * @date 2025/12/11
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @jakarta.annotation.Resource
    private UserService userService;

    /**
     * 登录
     * 
     * @param loginForm 登录表单
     * @return 用户信息（包含 token）
     */
    @PostMapping("/login")
    public Result<UserDetailVO> login(@RequestBody LoginForm loginForm) {
        UserDTO userDTO = userService.login(loginForm.getPhoneOrEmail(), loginForm.getPassword());
        UserDetailVO userVO = UserDetailVO.fromDTO(userDTO);
        return Result.success(userVO);
    }

    /**
     * 注册
     * 
     * @param registerForm 注册表单
     * @return 是否注册成功
     */
    @PostMapping("/register")
    public Result<Boolean> register(@RequestBody RegisterForm registerForm) {
        Boolean isRegister = userService.register(
            registerForm.getPhoneOrEmail(), 
            registerForm.getPassword(), 
            registerForm.getConfirmPassword()
        );
        if (!isRegister) {
            return Result.fail(ApiCode.PARAM_INVALID.getCode(), "注册失败");
        }
        return Result.success();
    }

    /**
     * 搜索用户（支持多条件查询）
     * 
     * @param searchForm 查询条件
     * @return 用户列表
     */
    @PostMapping("/search")
    public Result<List<UserPublicVO>> searchUser(@RequestBody UserSearchForm searchForm) {
        List<UserDTO> userDTOs = userService.searchUser(searchForm);
        if (userDTOs == null || userDTOs.isEmpty()) {
            return Result.fail(ApiCode.PARAM_INVALID.getCode(), "未找到符合条件的用户");
        }
        List<UserPublicVO> userVOs = userDTOs.stream()
                .map(UserPublicVO::fromDTO)
                .toList();
        return Result.success(userVOs);
    }
    
    /**
     * 退出登录
     * 
     * @param request 请求对象（从 Header 中获取 token）
     * @return 是否退出成功
     */
    @PostMapping("/logout")
    public Result<Boolean> logout(jakarta.servlet.http.HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            Boolean success = userService.logout(token);
            
            if (success) {
                return Result.success(true);
            }
        }
        
        return Result.fail(ApiCode.PARAM_INVALID.getCode(), "退出登录失败");
    }

    /**
     * 获取当前用户信息
     * 
     * @return 用户详情
     */
    @PostMapping("/profile")
    public Result<UserDetailVO> getProfile() {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.fail(ApiCode.UNAUTHORIZED.getCode(), "未登录");
        }
        
        UserDTO userDTO = userService.getUserById(userId);
        if (userDTO == null) {
            return Result.fail(ApiCode.PARAM_INVALID.getCode(), "用户不存在");
        }
        UserDetailVO userVO = UserDetailVO.fromDTO(userDTO);
        return Result.success(userVO);
    }

    /**
     * 更新用户信息
     * 
     * @param userDTO 用户信息
     * @return 是否更新成功
     */
    @PostMapping("/update")
    public Result<Boolean> updateProfile(@RequestBody UserDTO userDTO) {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.fail(ApiCode.UNAUTHORIZED.getCode(), "未登录");
        }
        
        userDTO.setUserId(userId);
        Boolean success = userService.updateUser(userDTO);
        if (!success) {
            return Result.fail(ApiCode.PARAM_INVALID.getCode(), "更新失败");
        }
        return Result.success(true);
    }

    /**
     * 统计用户的评论数量
     * 
     * @return 评论数量
     */
    @PostMapping("/count-comments")
    public Result<Integer> countComments() {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.fail(ApiCode.UNAUTHORIZED.getCode(), "未登录");
        }
        
        Integer count = userService.countUserComments(userId);
        return Result.success(count);
    }

    /**
     * 修改密码
     * 
     * @param params 包含旧密码和新密码
     * @return 是否修改成功
     */
    @PostMapping("/change-password")
    public Result<Boolean> changePassword(@RequestBody java.util.Map<String, String> params) {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.fail(ApiCode.UNAUTHORIZED.getCode(), "未登录");
        }
        
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");
        
        Boolean success = userService.changePassword(userId, oldPassword, newPassword);
        if (!success) {
            return Result.fail(ApiCode.PARAM_INVALID.getCode(), "修改密码失败");
        }
        return Result.success(true);
    }

    /**
     * 修改手机号
     * 
     * @param params 包含旧手机号、新手机号和密码
     * @return 是否修改成功
     */
    @PostMapping("/change-phone")
    public Result<Boolean> changePhone(@RequestBody java.util.Map<String, String> params) {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.fail(ApiCode.UNAUTHORIZED.getCode(), "未登录");
        }
        
        String oldPhone = params.get("oldPhone");
        String newPhone = params.get("newPhone");
        String password = params.get("password");
        
        Boolean success = userService.changePhone(userId, oldPhone, newPhone, password);
        if (!success) {
            return Result.fail(ApiCode.PARAM_INVALID.getCode(), "修改手机号失败");
        }
        return Result.success(true);
    }

    /**
     * 修改邮箱
     * 
     * @param params 包含旧邮箱、新邮箱和密码
     * @return 是否修改成功
     */
    @PostMapping("/change-email")
    public Result<Boolean> changeEmail(@RequestBody java.util.Map<String, String> params) {
        String userId = UserContext.getCurrentUserId();
        if (userId == null) {
            return Result.fail(ApiCode.UNAUTHORIZED.getCode(), "未登录");
        }
        
        String oldEmail = params.get("oldEmail");
        String newEmail = params.get("newEmail");
        String password = params.get("password");
        
        Boolean success = userService.changeEmail(userId, oldEmail, newEmail, password);
        if (!success) {
            return Result.fail(ApiCode.PARAM_INVALID.getCode(), "修改邮箱失败");
        }
        return Result.success(true);
    }
}
