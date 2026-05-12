package io.github.jacorycyjin.smartlibrary.backend.form;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

/**
 * 重置密码表单
 * 
 * @author Jacory
 * @date 2025/01/12
 */
@Data
public class ResetPasswordForm {

    /**
     * 手机号或邮箱
     */
    @NotBlank(message = "手机号或邮箱不能为空")
    private String phoneOrEmail;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    private String newPassword;

    /**
     * 确认新密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
