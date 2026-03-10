package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.ResourceLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

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
}
