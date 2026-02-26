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
}
