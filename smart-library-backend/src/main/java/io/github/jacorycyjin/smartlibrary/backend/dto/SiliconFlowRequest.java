package io.github.jacorycyjin.smartlibrary.backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * 硅基流动 API 请求体
 *
 * @author jcy
 * @date 2026/03/10
 */
@Data
public class SiliconFlowRequest {
    
    private String model;
    private List<Message> messages;
    private Double temperature = 0.1;
    
    @JsonProperty("response_format")
    private ResponseFormat responseFormat = new ResponseFormat();
    
    @Data
    public static class Message {
        private String role;
        private String content;
        
        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
    
    @Data
    public static class ResponseFormat {
        private String type = "json_object";
    }
}
