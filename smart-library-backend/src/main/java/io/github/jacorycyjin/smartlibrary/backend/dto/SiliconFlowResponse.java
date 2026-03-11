package io.github.jacorycyjin.smartlibrary.backend.dto;

import lombok.Data;

import java.util.List;

/**
 * 硅基流动 API 响应体
 *
 * @author jcy
 * @date 2026/03/10
 */
@Data
public class SiliconFlowResponse {
    
    private String id;
    private String object;
    private Long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    
    @Data
    public static class Choice {
        private Integer index;
        private Message message;
        private String finishReason;
    }
    
    @Data
    public static class Message {
        private String role;
        private String content;
    }
    
    @Data
    public static class Usage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
    }
}
