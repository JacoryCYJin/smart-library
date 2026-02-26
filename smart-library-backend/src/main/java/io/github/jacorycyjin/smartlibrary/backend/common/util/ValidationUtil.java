package io.github.jacorycyjin.smartlibrary.backend.common.util;

import org.springframework.util.StringUtils;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;

/**
 * @author Jacory
 * @date 2025/12/11
 */
public class ValidationUtil {

    // 正则表达式常量
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]+$";

    private ValidationUtil() {
        // 私有构造器，防止实例化
    }

    /**
     * 验证参数是否为空
     * 
     * @param value     参数值
     * @param fieldName 字段名称
     * @throws BusinessException 参数为空时抛出异常
     */
    public static void validateNotEmpty(String value, String fieldName) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), fieldName + "不能为空");
        }
    }

    /**
     * 验证邮箱格式
     * 
     * @param email 邮箱地址
     * @throws BusinessException 邮箱格式不正确时抛出异常
     */
    public static void validateEmailFormat(String email) {
        validateNotEmpty(email, "邮箱");
        if (!email.matches(EMAIL_REGEX)) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "邮箱格式不正确");
        }
    }

    /**
     * 验证手机号格式
     * 
     * @param phone 手机号
     * @throws BusinessException 手机号格式不正确时抛出异常
     */
    public static void validatePhoneFormat(String phone) {
        validateNotEmpty(phone, "手机号");
        if (!phone.matches(PHONE_REGEX)) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "手机号格式不正确");
        }
    }

    /** 
     * 验证密码格式
     * 
     * @param password 密码
     * @throws BusinessException 密码格式错误时抛出异常
     */
    public static void validatePasswordFormat(String password) {
        validateNotEmpty(password, "密码");
        if (!password.matches(PASSWORD_REGEX)) {
            throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "密码必须同时包含字母和数字");
        }
    }

    /**
     * 验证手机号或邮箱格式
     * 
     * @param phoneOrEmail 手机号或邮箱
     * @return true表示是邮箱，false表示是手机号
     * @throws BusinessException 格式不正确时抛出异常
     */
    public static boolean validatePhoneOrEmailFormat(String phoneOrEmail) {
        validateNotEmpty(phoneOrEmail, "手机号 / 邮箱");
        boolean isEmail = phoneOrEmail.contains("@");
        boolean isPhone = phoneOrEmail.matches(PHONE_REGEX);

        if (isEmail) {
            validateEmailFormat(phoneOrEmail);
            return true;
        } else if (isPhone) {
            validatePhoneFormat(phoneOrEmail);
            return false;
        }
        throw new BusinessException(ApiCode.PARAM_INVALID.getCode(), "手机号 / 邮箱格式不正确");
    }

}
