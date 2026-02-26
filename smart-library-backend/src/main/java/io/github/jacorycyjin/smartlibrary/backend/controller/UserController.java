package io.github.jacorycyjin.smartlibrary.backend.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.jacorycyjin.smartlibrary.backend.service.UserService;
import io.github.jacorycyjin.smartlibrary.backend.dto.UserDTO;
import io.github.jacorycyjin.smartlibrary.backend.form.LoginForm;
import io.github.jacorycyjin.smartlibrary.backend.form.RegisterForm;
import io.github.jacorycyjin.smartlibrary.backend.form.UserSearchForm;
import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.vo.UserPublicVO;
import io.github.jacorycyjin.smartlibrary.backend.vo.UserDetailVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
}
