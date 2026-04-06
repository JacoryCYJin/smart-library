package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;

import java.util.Map;

/**
 * 管理员 AI 情感走向管理服务
 *
 * @author jcy
 * @date 2026/04/06
 */
public interface AdminEmotionArcService {

    /**
     * 分页查询情感走向列表
     */
    PageDTO<Map<String, Object>> getEmotionArcList(Map<String, Object> params);

    /**
     * 触发情感走向生成
     *
     * @param resourceId    资源ID
     * @param forceGenerate 是否强制生成（跳过体裁判定）
     */
    void triggerEmotionArcGeneration(String resourceId, Boolean forceGenerate);

    /**
     * 重试情感走向生成
     */
    void retryEmotionArcGeneration(String arcId);

    /**
     * 删除情感走向
     */
    void deleteEmotionArc(String arcId);

    /**
     * 获取情感走向详情
     */
    Map<String, Object> getEmotionArcDetail(String arcId);
}
