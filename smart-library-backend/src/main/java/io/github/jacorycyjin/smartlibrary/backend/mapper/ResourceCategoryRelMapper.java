package io.github.jacorycyjin.smartlibrary.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 资源-分类关联 Mapper
 * 
 * @author Jacory
 * @date 2025/04/11
 */
@Mapper
public interface ResourceCategoryRelMapper {

    /**
     * 根据资源ID列表查询分类ID列表（去重）
     * 
     * @param resourceIds 资源ID列表
     * @return 分类ID列表
     */
    List<String> selectCategoryIdsByResourceIds(@Param("resourceIds") List<String> resourceIds);
}
