package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;

import java.util.Map;

/**
 * 管理员 AI 图谱管理服务接口
 * 
 * @author Kiro
 * @date 2026/04/05
 */
public interface AdminGraphService {
    
    /**
     * 获取 AI 图谱列表
     * 
     * @param params 查询参数
     * @return 分页数据
     */
    PageDTO getGraphList(Map<String, Object> params);
    
    /**
     * 手动触发图谱生成
     * 
     * @param resourceId 资源ID
     * @param forceGenerate 是否强制生成（跳过AI体裁判定）
     */
    void triggerGraphGeneration(String resourceId, Boolean forceGenerate);
    
    /**
     * 重试失败的图谱生成
     * 
     * @param graphId 图谱ID
     */
    void retryGraphGeneration(String graphId);
    
    /**
     * 删除图谱
     * 
     * @param graphId 图谱ID
     */
    void deleteGraph(String graphId);
    
    /**
     * 获取图谱详情
     * 
     * @param graphId 图谱ID
     * @return 图谱详情
     */
    Map<String, Object> getGraphDetail(String graphId);
}
