package io.github.jacorycyjin.smartlibrary.backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 硅基流动 AI 配置类
 *
 * @author jcy
 * @date 2026/03/10
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "siliconflow")
public class SiliconFlowConfig {
    
    /**
     * API 密钥
     */
    private String apiKey;
    
    /**
     * API 网关地址
     */
    private String apiUrl;
    
    /**
     * 模型标识
     */
    private String model;
    
    /**
     * 超时时间（毫秒）
     */
    private Integer timeout = 60000;
    
    /**
     * 配置 RestTemplate Bean
     */
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        return new RestTemplate(factory);
    }
}
