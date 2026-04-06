package io.github.jacorycyjin.smartlibrary.backend.service;

import java.util.Map;

/**
 * 情感走向生成服务
 *
 * @author jcy
 * @date 2026/04/06
 */
public interface EmotionArcService {

    /**
     * 生成并保存情感走向（异步，普通模式：AI 智能判断）
     *
     * @param resourceId 资源ID
     */
    void generateAndSaveEmotionArc(String resourceId);

    /**
     * 生成并保存情感走向（同步，普通模式：AI 智能判断）
     * 用于管理员手动触发生成
     *
     * @param resourceId 资源ID
     * @return 情感走向ID
     */
    String generateAndSaveEmotionArcSync(String resourceId);

    /**
     * 生成并保存情感走向（异步，强制模式：跳过体裁判定）
     *
     * @param resourceId 资源ID
     */
    void generateAndSaveEmotionArcForce(String resourceId);

    /**
     * 生成并保存情感走向（同步，强制模式：跳过体裁判定）
     * 用于管理员手动触发强制生成
     *
     * @param resourceId 资源ID
     * @return 情感走向ID
     */
    String generateAndSaveEmotionArcForceSync(String resourceId);

    /**
     * 查询情感走向生成状态
     *
     * @param resourceId 资源ID
     * @return 生成状态：0-未生成, 1-生成中, 2-生成成功, 3-生成失败
     */
    Integer getEmotionArcStatus(String resourceId);

    /**
     * 获取情感走向 JSON 数据
     *
     * @param resourceId 资源ID
     * @return JSON 字符串
     */
    String getEmotionArcJson(String resourceId);

    /**
     * 根据资源ID获取情感走向数据
     *
     * @param resourceId 资源ID
     * @return 情感走向数据（包含 chapterCount 和 data），如果不存在或未生成成功则返回 null
     */
    Map<String, Object> getEmotionArcByResourceId(String resourceId);
}
