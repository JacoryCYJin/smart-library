package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.Author;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 作者 Mapper
 * 
 * @author Jacory
 * @date 2025/01/27
 */
@Mapper
public interface AuthorMapper {

    /**
     * 根据资源ID查询作者列表（按 sort 排序）
     * 
     * @param resourceId 资源ID
     * @return 作者信息列表（包含 sort 字段）
     */
    List<Map<String, Object>> selectAuthorsByResourceId(@Param("resourceId") String resourceId);

    /**
     * 批量查询资源的作者（按 sort 排序）
     * 
     * @param resourceIds 资源ID列表
     * @return 作者信息列表（包含 resourceId 和 sort 字段）
     */
    List<Map<String, Object>> selectAuthorsByResourceIds(@Param("resourceIds") List<String> resourceIds);

    /**
     * 根据作者ID查询作者信息
     * 
     * @param authorId 作者ID
     * @return 作者信息
     */
    Author selectAuthorById(@Param("authorId") String authorId);

    /**
     * 统一查询作者列表（支持多条件动态查询）
     * 
     * @param params 查询参数
     * @return 作者列表
     */
    List<Author> searchAuthors(Map<String, Object> params);

    /**
     * 统计作者数量
     * 
     * @param params 查询参数
     * @return 作者数量
     */
    int countAuthors(Map<String, Object> params);

    /**
     * 根据关键词搜索作者（支持分页）
     * 
     * @param keyword 搜索关键词
     * @param offset 偏移量
     * @param limit 返回数量限制
     * @return 作者列表
     */
    List<Author> searchAuthorsByKeyword(@Param("keyword") String keyword, 
                                        @Param("offset") Integer offset,
                                        @Param("limit") Integer limit);

    /**
     * 插入作者
     * 
     * @param author 作者信息
     * @return 影响行数
     */
    int insert(Author author);

    /**
     * 更新作者
     * 
     * @param author 作者信息
     * @return 影响行数
     */
    int update(Author author);

    /**
     * 根据作者ID查询关联的资源数量
     * 
     * @param authorId 作者ID
     * @return 资源数量
     */
    int countResourcesByAuthorId(@Param("authorId") String authorId);
}
