package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.ResourceCharacterGraph;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * AI人物关系图谱 Mapper
 * 
 * @author Jacory
 * @date 2026/04/05
 */
@Mapper
public interface ResourceCharacterGraphMapper {

    /**
     * 根据资源ID查询图谱
     * 
     * @param resourceId 资源ID
     * @return 图谱信息
     */
    ResourceCharacterGraph selectByResourceId(@Param("resourceId") String resourceId);

    /**
     * 统一查询图谱列表（支持多条件动态查询）
     * 
     * @param params 查询参数
     * @return 图谱列表
     */
    List<ResourceCharacterGraph> searchGraphs(Map<String, Object> params);

    /**
     * 统计图谱数量
     * 
     * @param params 查询参数
     * @return 图谱数量
     */
    int countGraphs(Map<String, Object> params);

    /**
     * 根据图谱ID查询图谱
     * 
     * @param graphId 图谱ID
     * @return 图谱信息
     */
    ResourceCharacterGraph selectByGraphId(@Param("graphId") String graphId);

    /**
     * 插入图谱
     * 
     * @param graph 图谱信息
     * @return 影响行数
     */
    int insert(ResourceCharacterGraph graph);

    /**
     * 更新图谱
     * 
     * @param graph 图谱信息
     * @return 影响行数
     */
    int update(ResourceCharacterGraph graph);

    /**
     * 删除图谱（软删除）
     * 
     * @param graphId 图谱ID
     * @return 影响行数
     */
    int deleteById(@Param("graphId") String graphId);
}
