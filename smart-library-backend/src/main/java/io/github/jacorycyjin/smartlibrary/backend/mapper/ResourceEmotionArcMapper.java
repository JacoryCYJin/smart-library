package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.ResourceEmotionArc;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 资源情感走向 Mapper
 *
 * @author jcy
 * @date 2026/04/06
 */
@Mapper
public interface ResourceEmotionArcMapper {
    
    /**
     * 根据资源ID查询情感走向
     */
    ResourceEmotionArc selectByResourceId(@Param("resourceId") String resourceId);
    
    /**
     * 插入情感走向记录
     */
    int insert(ResourceEmotionArc arc);
    
    /**
     * 更新情感走向数据
     */
    int updateArcData(@Param("resourceId") String resourceId,
                      @Param("arcJson") String arcJson,
                      @Param("generateStatus") Integer generateStatus,
                      @Param("errorMessage") String errorMessage,
                      @Param("aiModel") String aiModel,
                      @Param("tokenUsage") Integer tokenUsage,
                      @Param("generateTime") Integer generateTime);
    
    /**
     * 更新生成状态
     */
    int updateGenerateStatus(@Param("resourceId") String resourceId,
                             @Param("generateStatus") Integer generateStatus,
                             @Param("errorMessage") String errorMessage);
    
    /**
     * 删除情感走向（逻辑删除）
     */
    int deleteByResourceId(@Param("resourceId") String resourceId);
    
    /**
     * 根据 arcId 查询情感走向
     */
    ResourceEmotionArc selectByArcId(@Param("arcId") String arcId);
    
    /**
     * 根据 arcId 删除情感走向（逻辑删除）
     */
    int deleteByArcId(@Param("arcId") String arcId);
    
    /**
     * 统计情感走向数量
     */
    int countEmotionArcs(java.util.Map<String, Object> params);
    
    /**
     * 搜索情感走向列表
     */
    java.util.List<ResourceEmotionArc> searchEmotionArcs(java.util.Map<String, Object> params);
}
