package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.dto.CharacterGraphDTO;

/**
 * AI 人物关系图谱服务接口
 *
 * @author jcy
 * @date 2026/03/10
 */
public interface CharacterGraphService {
    
    /**
     * 根据书籍简介生成人物关系图谱
     *
     * @param bookDescription 书籍简介文本
     * @return 人物关系图谱数据
     */
    CharacterGraphDTO generateCharacterGraph(String bookDescription);
    
    /**
     * 为指定资源生成并保存人物关系图谱
     *
     * @param resourceId 资源ID
     * @return 图谱ID，如果不适合生成图谱则返回 null
     */
    String generateAndSaveGraph(String resourceId);
    
    /**
     * 为指定资源强制生成并保存人物关系图谱（跳过体裁判定）
     * 用于管理员手动触发的强制生成场景
     *
     * @param resourceId 资源ID
     * @return 图谱ID
     */
    String generateAndSaveGraphForce(String resourceId);
    
    /**
     * 根据资源ID获取人物关系图谱
     *
     * @param resourceId 资源ID
     * @return 人物关系图谱数据，如果不存在则返回 null
     */
    CharacterGraphDTO getGraphByResourceId(String resourceId);
}
