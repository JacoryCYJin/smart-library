package io.github.jacorycyjin.smartlibrary.backend.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jacory
 * @date 2025/12/12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginForm {

    /**
     * 手机号或邮箱
     */
    private String phoneOrEmail;

    /**
     * 密码
     */
    private String password;

}
