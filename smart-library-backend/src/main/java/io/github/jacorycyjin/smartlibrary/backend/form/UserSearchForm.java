package io.github.jacorycyjin.smartlibrary.backend.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Jacory
 * @date 2025/12/28
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSearchForm {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号或邮箱
     */
    private String phoneOrEmail;

    /**
     * 角色
     */
    private Integer role;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 是否删除
     */
    private Integer deleted;

    /**
     * 查询数量限制
     */
    private Integer limit;
}
