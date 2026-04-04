package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 资源 Mapper
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@Mapper
public interface ResourceMapper {

    /**
     * 通用搜索资源方法（支持多条件动态查询）
     * 
     * @param params 查询参数 Map
     *               - resourceId: 单个资源业务ID
     *               - type: 资源类型
     *               - keyword: 关键词（标题、作者、摘要）
     *               - authorName: 作者名称
     *               - publisher: 出版社
     *               - journal: 期刊名称
     *               - categoryIds: 分类ID列表
     *               - tagIds: 标签ID列表
     *               - sortBy: 排序字段
     *               - limit: 查询数量限制
     * @return 资源列表
     */
    List<Resource> searchResources(Map<String, Object> params);

    /**
     * 插入资源
     * 
     * @param resource 资源实体
     * @return 影响行数
     */
    int insert(Resource resource);

    /**
     * 更新资源
     * 
     * @param resource 资源实体
     * @return 影响行数
     */
    int update(Resource resource);

    /**
     * 增加浏览量
     * 
     * @param resourceId 资源ID
     * @return 影响行数
     */
    int incrementViewCount(@Param("resourceId") String resourceId);

    /**
     * 增加评论数
     * 
     * @param resourceId 资源ID
     * @return 影响行数
     */
    int incrementCommentCount(@Param("resourceId") String resourceId);

    /**
     * 减少评论数
     * 
     * @param resourceId 资源ID
     * @return 影响行数
     */
    int decrementCommentCount(@Param("resourceId") String resourceId);

    /**
     * 增加收藏数
     * 
     * @param resourceId 资源ID
     * @return 影响行数
     */
    int incrementStarCount(@Param("resourceId") String resourceId);

    /**
     * 减少收藏数
     * 
     * @param resourceId 资源ID
     * @return 影响行数
     */
    int decrementStarCount(@Param("resourceId") String resourceId);

    /**
     * 根据作者ID查询该作者的所有作品
     * 
     * @param authorId 作者ID
     * @return 资源列表
     */
    List<Resource> selectResourcesByAuthorId(@Param("authorId") String authorId);

    /**
     * 更新资源的用户评分和评分人数
     * 
     * @param resourceId 资源ID
     * @param userScore 用户平均评分
     * @param userScoreCount 评分人数
     * @return 影响行数
     */
    int updateUserScore(@Param("resourceId") String resourceId, 
                        @Param("userScore") java.math.BigDecimal userScore,
                        @Param("userScoreCount") Integer userScoreCount);

    /**
     * 根据资源ID查询单个资源
     * 
     * @param resourceId 资源ID
     * @return 资源实体
     */
    Resource selectById(@Param("resourceId") String resourceId);

    /**
     * 根据资源ID查询人物关系图谱
     * 
     * @param resourceId 资源ID
     * @return 图谱实体
     */
    io.github.jacorycyjin.smartlibrary.backend.entity.ResourceCharacterGraph selectGraphByResourceId(@Param("resourceId") String resourceId);

    /**
     * 插入人物关系图谱
     * 
     * @param graph 图谱实体
     * @return 影响行数
     */
    int insertGraph(io.github.jacorycyjin.smartlibrary.backend.entity.ResourceCharacterGraph graph);

    /**
     * 更新人物关系图谱
     * 
     * @param graph 图谱实体
     * @return 影响行数
     */
    int updateGraph(io.github.jacorycyjin.smartlibrary.backend.entity.ResourceCharacterGraph graph);

    /**
     * 更新资源的 has_graph 字段
     * 
     * @param resourceId 资源ID
     * @param hasGraph 是否有图谱（0-无 / 1-有）
     * @return 影响行数
     */
    int updateHasGraph(@Param("resourceId") String resourceId, @Param("hasGraph") Integer hasGraph);

    /**
     * 根据资源ID查询资源
     * 
     * @param resourceId 资源业务ID
     * @return 资源信息
     */
    Resource findByResourceId(@Param("resourceId") String resourceId);

    /**
     * 根据资源ID更新资源
     * 
     * @param resource 资源信息
     * @return 影响行数
     */
    int updateByResourceId(Resource resource);

    /**
     * 统计资源数量
     * 
     * @param params 查询参数
     * @return 资源数量
     */
    Long countByParams(Map<String, Object> params);
}
