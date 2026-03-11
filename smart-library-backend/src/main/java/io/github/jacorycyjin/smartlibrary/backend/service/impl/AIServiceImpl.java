package io.github.jacorycyjin.smartlibrary.backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.jacorycyjin.smartlibrary.backend.common.constant.AIPromptConstants;
import io.github.jacorycyjin.smartlibrary.backend.common.enums.ApiCode;
import io.github.jacorycyjin.smartlibrary.backend.common.exception.BusinessException;
import io.github.jacorycyjin.smartlibrary.backend.config.SiliconFlowConfig;
import io.github.jacorycyjin.smartlibrary.backend.dto.SiliconFlowRequest;
import io.github.jacorycyjin.smartlibrary.backend.dto.SiliconFlowResponse;
import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import io.github.jacorycyjin.smartlibrary.backend.mapper.ResourceMapper;
import io.github.jacorycyjin.smartlibrary.backend.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * AI 通用服务实现
 *
 * @author jcy
 * @date 2026/03/10
 */
@Slf4j
@Service
public class AIServiceImpl implements AIService {
    
    @jakarta.annotation.Resource
    private SiliconFlowConfig siliconFlowConfig;
    
    @jakarta.annotation.Resource
    private RestTemplate restTemplate;
    
    @jakarta.annotation.Resource
    private ResourceMapper resourceMapper;
    
    @jakarta.annotation.Resource
    private ObjectMapper objectMapper;
    
    @Override
    public String chat(String systemPrompt, String userPrompt) {
        try {
            // 构建请求
            SiliconFlowRequest request = buildRequest(systemPrompt, userPrompt, false);
            
            // 调用 API
            String content = callAPI(request);
            
            return content;
            
        } catch (Exception e) {
            log.error("AI 对话失败", e);
            throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "AI 对话失败: " + e.getMessage());
        }
    }
    
    @Override
    public <T> T chatJson(String systemPrompt, String userPrompt, Class<T> responseClass) {
        try {
            // 构建请求（JSON 模式）
            SiliconFlowRequest request = buildRequest(systemPrompt, userPrompt, true);
            
            // 调用 API
            String content = callAPI(request);
            
            // ========== 详细分析原始响应 ==========
            log.info("========================================");
            log.info("原始 JSON 响应分析");
            log.info("========================================");
            log.info("原始内容: {}", content);
            log.info("原始长度: {} 字符", content.length());
            
            // 打印每个字符的十六进制表示（前 50 个字符）
            StringBuilder hexDump = new StringBuilder();
            int dumpLength = Math.min(content.length(), 50);
            for (int i = 0; i < dumpLength; i++) {
                char c = content.charAt(i);
                hexDump.append(String.format("[%c=0x%04X] ", c, (int) c));
            }
            log.info("前 50 字符的十六进制: {}", hexDump.toString());
            log.info("========================================");
            
            // 清理 JSON 字符串：移除控制字符（Tab、换行、回车等）
            String cleanedContent = content
                .replaceAll("\\t", "")      // 移除 Tab
                .replaceAll("\\r", "")      // 移除回车
                .replaceAll("\\n", "")      // 移除换行
                .replaceAll("\\s{2,}", " ") // 多个空格替换为单个空格
                .trim();
            
            log.info("清理后的 JSON: {}", cleanedContent);
            log.info("清理后长度: {} 字符", cleanedContent.length());
            
            // 检查 JSON 是否有效（至少包含 nodes 和 edges）
            if (!cleanedContent.contains("nodes") || !cleanedContent.contains("edges")) {
                log.warn("AI 返回的 JSON 格式无效，缺少 nodes 或 edges 字段");
                log.warn("将返回空图谱对象");
                // 返回空的图谱对象
                return objectMapper.readValue("{\"nodes\": [], \"edges\": []}", responseClass);
            }
            
            // 尝试修复常见的 JSON 格式问题
            // 修复：{" : [], "edges": []} -> {"nodes": [], "edges": []}
            String fixedContent = cleanedContent.replaceAll("\\{\"\\s*:", "{\"nodes\":");
            
            if (!fixedContent.equals(cleanedContent)) {
                log.info("JSON 已自动修复");
                log.info("修复后的 JSON: {}", fixedContent);
            }
            
            // 反序列化
            T result = objectMapper.readValue(fixedContent, responseClass);
            
            log.info("JSON 解析成功");
            log.info("========================================");
            
            return result;
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("AI JSON 对话失败");
            log.error("错误类型: {}", e.getClass().getName());
            log.error("错误信息: {}", e.getMessage());
            log.error("========================================", e);
            
            // 如果解析失败，返回空的图谱对象而不是抛出异常
            try {
                log.warn("JSON 解析失败，返回空图谱");
                return objectMapper.readValue("{\"nodes\": [], \"edges\": []}", responseClass);
            } catch (Exception ex) {
                throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "AI JSON 对话失败: " + e.getMessage());
            }
        }
    }
    
    @Override
    public String answerBookQuestion(String resourceId, String question) {
        // 1. 查询资源信息
        Resource resource = resourceMapper.selectById(resourceId);
        if (resource == null) {
            throw new BusinessException(ApiCode.RESOURCE_NOT_FOUND.getCode(), "资源不存在");
        }
        
        // 2. 构建提示词
        String userPrompt = String.format(
            AIPromptConstants.QA_USER_PROMPT_TEMPLATE,
            resource.getTitle(),
            resource.getAuthorName(),
            resource.getSummary(),
            question
        );
        
        // 3. 调用 AI
        return chat(AIPromptConstants.QA_SYSTEM_PROMPT, userPrompt);
    }
    
    /**
     * 构建请求体
     */
    private SiliconFlowRequest buildRequest(String systemPrompt, String userPrompt, boolean jsonMode) {
        SiliconFlowRequest request = new SiliconFlowRequest();
        request.setModel(siliconFlowConfig.getModel());
        request.setTemperature(0.1);
        
        // 如果不是 JSON 模式，不设置 response_format
        if (!jsonMode) {
            request.setResponseFormat(null);
        }
        
        List<SiliconFlowRequest.Message> messages = new ArrayList<>();
        messages.add(new SiliconFlowRequest.Message("system", systemPrompt));
        messages.add(new SiliconFlowRequest.Message("user", userPrompt));
        
        request.setMessages(messages);
        
        return request;
    }
    
    /**
     * 调用 API
     */
    private String callAPI(SiliconFlowRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            // ========== 详细日志：请求信息 ==========
            log.info("========================================");
            log.info("硅基流动 API 请求详情");
            log.info("========================================");
            log.info("API 地址: {}", siliconFlowConfig.getApiUrl());
            log.info("模型: {}", siliconFlowConfig.getModel());
            log.info("Temperature: {}", request.getTemperature());
            log.info("Response Format: {}", request.getResponseFormat() != null ? request.getResponseFormat().getType() : "text");
            log.info("----------------------------------------");
            
            // 打印 messages 内容
            if (request.getMessages() != null) {
                for (int i = 0; i < request.getMessages().size(); i++) {
                    SiliconFlowRequest.Message msg = request.getMessages().get(i);
                    log.info("Message[{}] - Role: {}", i, msg.getRole());
                    
                    // System Prompt 只打印前 200 字
                    if ("system".equals(msg.getRole())) {
                        String preview = msg.getContent().length() > 200 
                            ? msg.getContent().substring(0, 200) + "..." 
                            : msg.getContent();
                        log.info("Content (前200字): {}", preview);
                    } else {
                        // User Prompt 完整打印
                        log.info("Content: {}", msg.getContent());
                    }
                    log.info("----------------------------------------");
                }
            }
            
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(siliconFlowConfig.getApiKey());
            
            HttpEntity<SiliconFlowRequest> entity = new HttpEntity<>(request, headers);
            
            // 调用 API
            log.info("正在发送请求到硅基流动...");
            ResponseEntity<SiliconFlowResponse> response = restTemplate.postForEntity(
                siliconFlowConfig.getApiUrl(),
                entity,
                SiliconFlowResponse.class
            );
            
            long endTime = System.currentTimeMillis();
            
            // ========== 详细日志：响应信息 ==========
            log.info("========================================");
            log.info("硅基流动 API 响应详情");
            log.info("========================================");
            log.info("HTTP 状态码: {}", response.getStatusCode());
            log.info("耗时: {} ms", endTime - startTime);
            
            // 解析响应
            if (response.getBody() == null || response.getBody().getChoices() == null || response.getBody().getChoices().isEmpty()) {
                log.error("API 响应为空或格式错误");
                throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "API 响应为空");
            }
            
            // Token 使用情况
            if (response.getBody().getUsage() != null) {
                log.info("Token 使用情况:");
                log.info("  - Prompt Tokens: {}", response.getBody().getUsage().getPromptTokens());
                log.info("  - Completion Tokens: {}", response.getBody().getUsage().getCompletionTokens());
                log.info("  - Total Tokens: {}", response.getBody().getUsage().getTotalTokens());
            }
            
            String content = response.getBody().getChoices().get(0).getMessage().getContent();
            
            // 响应内容预览
            String contentPreview = content.length() > 300 
                ? content.substring(0, 300) + "..." 
                : content;
            log.info("响应内容 (前300字): {}", contentPreview);
            log.info("响应内容总长度: {} 字符", content.length());
            log.info("========================================");
            
            return content;
            
        } catch (Exception e) {
            log.error("========================================");
            log.error("硅基流动 API 调用失败");
            log.error("========================================");
            log.error("错误类型: {}", e.getClass().getName());
            log.error("错误信息: {}", e.getMessage());
            log.error("========================================", e);
            throw new BusinessException(ApiCode.SERVER_ERROR.getCode(), "API 调用失败: " + e.getMessage());
        }
    }
}
