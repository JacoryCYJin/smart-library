package io.github.jacorycyjin.smartlibrary.backend.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jacory
 * @date 2025/12/13
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterForm {

    /**
     * 手机号或邮箱
     */
    private String phoneOrEmail;

    /**
     * 密码
     */
    private String password;

    /**
     * 确认密码
     */
    private String confirmPassword;
}
