package io.github.jacorycyjin.smartlibrary.backend;

import io.github.jacorycyjin.smartlibrary.backend.common.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 密码加密测试工具
 * 用于生成 BCrypt 加密后的密码，方便数据库迁移
 * 
 * @author Jacory
 * @date 2025/12/14
 */
@SpringBootTest
public class PasswordEncoderTest {

    @Test
    public void testEncodePassword() {
        // 测试密码加密
        String rawPassword = "admin123";
        String encodedPassword = PasswordUtil.encode(rawPassword);
        
        System.out.println("原始密码: " + rawPassword);
        System.out.println("加密后: " + encodedPassword);
        System.out.println("验证结果: " + PasswordUtil.matches(rawPassword, encodedPassword));
    }

    @Test
    public void testGenerateCommonPasswords() {
        // 生成常用测试密码的加密值
        String[] passwords = {"test123", "admin123", "user123", "11111111q"};
        
        System.out.println("=== 常用测试密码加密结果 ===");
        for (String password : passwords) {
            String encoded = PasswordUtil.encode(password);
            System.out.println(password + " -> " + encoded);
        }
    }

    @Test
    public void testVerifyPassword() {
        // 测试密码验证
        String rawPassword = "test123";
        String encodedPassword = PasswordUtil.encode(rawPassword);
        
        System.out.println("正确密码验证: " + PasswordUtil.matches("test123", encodedPassword));
        System.out.println("错误密码验证: " + PasswordUtil.matches("wrong123", encodedPassword));
    }
}
