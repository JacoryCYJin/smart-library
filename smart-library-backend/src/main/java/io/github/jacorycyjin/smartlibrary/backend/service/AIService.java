package io.github.jacorycyjin.smartlibrary.backend.service;

/**
 * AI 通用服务接口
 *
 * @author jcy
 * @date 2026/03/10
 */
public interface AIService {
    
    /**
     * 调用 AI 模型（返回纯文本）
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt 用户提示词
     * @return AI 响应内容
     */
    String chat(String systemPrompt, String userPrompt);
    
    /**
     * 调用 AI 模型（返回 JSON）
     *
     * @param systemPrompt 系统提示词
     * @param userPrompt 用户提示词
     * @param responseClass 响应类型
     * @return 反序列化后的对象
     */
    <T> T chatJson(String systemPrompt, String userPrompt, Class<T> responseClass);
    
    /**
     * 回答用户关于书籍的问题
     *
     * @param resourceId 资源ID
     * @param question 用户问题
     * @return AI 回答
     */
    String answerBookQuestion(String resourceId, String question);
}
