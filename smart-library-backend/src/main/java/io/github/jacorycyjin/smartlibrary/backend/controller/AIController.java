package io.github.jacorycyjin.smartlibrary.backend.controller;

import io.github.jacorycyjin.smartlibrary.backend.common.response.Result;
import io.github.jacorycyjin.smartlibrary.backend.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * AI 通用 Controller
 *
 * @author jcy
 * @date 2026/03/10
 */
@Slf4j
@RestController
@RequestMapping("/ai")
public class AIController {
    
    @jakarta.annotation.Resource
    private AIService aiService;
    
    /**
     * 回答用户关于书籍的问题
     *
     * @param resourceId 资源ID
     * @param question 用户问题
     * @return AI 回答
     */
    @PostMapping("/ask/{resourceId}")
    public Result<String> askQuestion(
            @PathVariable String resourceId,
            @RequestBody String question) {
        log.info("收到用户提问，资源ID: {}, 问题: {}", resourceId, question);
        String answer = aiService.answerBookQuestion(resourceId, question);
        return Result.success(answer);
    }
}
