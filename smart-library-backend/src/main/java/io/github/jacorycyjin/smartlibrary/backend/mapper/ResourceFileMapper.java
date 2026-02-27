package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.ResourceFile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源文件 Mapper
 * 
 * @author Jacory
 * @date 2026/02/27
 */
@Mapper
public interface ResourceFileMapper {

    /**
     * 插入资源文件
     * 
     * @param resourceFile 资源文件实体
     * @return 影响行数
     */
    int insert(ResourceFile resourceFile);

    /**
     * 批量插入资源文件
     * 
     * @param resourceFiles 资源文件列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<ResourceFile> resourceFiles);

    /**
     * 根据资源ID查询文件列表
     * 
     * @param resourceId 资源业务ID
     * @return 文件列表
     */
    List<ResourceFile> selectByResourceId(@Param("resourceId") String resourceId);

    /**
     * 根据资源ID逻辑删除所有文件
     * 
     * @param resourceId 资源业务ID
     * @return 影响行数
     */
    int deleteByResourceId(@Param("resourceId") String resourceId);
}
