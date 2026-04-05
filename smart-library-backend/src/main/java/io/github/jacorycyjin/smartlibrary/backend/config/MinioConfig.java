package io.github.jacorycyjin.smartlibrary.backend.config;

import io.minio.MinioClient;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MinIO 配置类
 * 
 * @author JacoryCyJin
 * @date 2025/02/27
 */
@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "minio")
public class MinioConfig {
    
    private String endpoint;
    private String accessKey;
    private String secretKey;
    private Buckets buckets;
    
    @Data
    public static class Buckets {
        private String covers;
        private String authorAvatars;
        private String userAvatars;
        private String attachments;
        private String nlpCorpus;
        
        /**
         * 获取用户头像bucket（兼容方法）
         */
        public String getAvatars() {
            return userAvatars;
        }
    }
    
    /**
     * 创建 MinIO 客户端
     * 
     * @return MinioClient 实例
     */
    @Bean
    public MinioClient minioClient() {
        log.info("初始化 MinIO 客户端: endpoint={}, accessKey={}", endpoint, accessKey);
        log.info("Buckets 配置: covers={}, authorAvatars={}, userAvatars={}, attachments={}, nlpCorpus={}", 
                 buckets != null ? buckets.getCovers() : "null",
                 buckets != null ? buckets.getAuthorAvatars() : "null",
                 buckets != null ? buckets.getUserAvatars() : "null",
                 buckets != null ? buckets.getAttachments() : "null",
                 buckets != null ? buckets.getNlpCorpus() : "null");
        
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
