package io.github.jacorycyjin.smartlibrary.backend.common.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类
 * 
 * @author Jacory
 * @date 2025/02/25
 */
@Component
public class JwtUtil {
    
    // JWT 密钥（从配置文件读取）
    private final SecretKey secretKey;
    
    // Token 有效期（从配置文件读取，默认7天）
    @Value("${jwt.expiration:604800000}")
    private long expirationTime;
    
    // Token 续期阈值（从配置文件读取，默认3天）
    @Value("${jwt.refresh-threshold:259200000}")
    private long refreshThreshold;
    
    /**
     * 构造函数：从配置文件读取密钥
     */
    public JwtUtil(@Value("${jwt.secret}") String secret) {
        // 使用配置的密钥生成 SecretKey
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * 生成 JWT token
     * 
     * @param userId 用户ID
     * @param username 用户名
     * @return JWT token
     */
    public String generateToken(String userId, String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(secretKey)
                .compact();
    }
    
    /**
     * 从 token 中解析用户ID
     * 
     * @param token JWT token
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }
    
    /**
     * 从 token 中解析用户名
     * 
     * @param token JWT token
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        Claims claims = parseToken(token);
        return (String) claims.get("username");
    }
    
    /**
     * 验证 token 是否有效
     * 
     * @param token JWT token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 获取 token 的过期时间
     * 
     * @param token JWT token
     * @return 过期时间
     */
    public Date getExpirationFromToken(String token) {
        Claims claims = parseToken(token);
        return claims.getExpiration();
    }
    
    /**
     * 检查 token 是否需要续期
     * 
     * @param token JWT token
     * @return 是否需要续期
     */
    public boolean shouldRefreshToken(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            long remainingTime = expiration.getTime() - System.currentTimeMillis();
            // 剩余有效期小于阈值时需要续期
            return remainingTime > 0 && remainingTime < refreshThreshold;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 刷新 token（生成新的 token）
     * 
     * @param token 旧 token
     * @return 新 token
     */
    public String refreshToken(String token) {
        try {
            String userId = getUserIdFromToken(token);
            String username = getUsernameFromToken(token);
            return generateToken(userId, username);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 解析 token
     * 
     * @param token JWT token
     * @return Claims
     */
    private Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
