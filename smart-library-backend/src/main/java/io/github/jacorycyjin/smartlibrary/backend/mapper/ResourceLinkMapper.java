package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.ResourceLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 资源链接 Mapper
 * 
 * @author Jacory
 * @date 2026/03/10
 */
@Mapper
public interface ResourceLinkMapper {

    /**
     * 根据资源ID查询所有链接
     * 
     * @param resourceId 资源ID
     * @return 链接列表
     */
    List<ResourceLink> selectLinksByResourceId(@Param("resourceId") String resourceId);

    /**
     * 统一查询资源链接列表（支持多条件动态查询）
     * 
     * @param params 查询参数
     * @return 链接列表
     */
    List<ResourceLink> searchLinks(Map<String, Object> params);

    /**
     * 统计链接数量
     * 
     * @param params 查询参数
     * @return 链接数量
     */
    int countLinks(Map<String, Object> params);

    /**
     * 根据链接ID查询链接
     * 
     * @param linkId 链接ID
     * @return 链接信息
     */
    ResourceLink selectLinkById(@Param("linkId") String linkId);

    /**
     * 插入链接
     * 
     * @param link 链接信息
     * @return 影响行数
     */
    int insert(ResourceLink link);

    /**
     * 更新链接
     * 
     * @param link 链接信息
     * @return 影响行数
     */
    int update(ResourceLink link);

    /**
     * 删除链接（软删除）
     * 
     * @param linkId 链接ID
     * @return 影响行数
     */
    int deleteById(@Param("linkId") String linkId);
}
