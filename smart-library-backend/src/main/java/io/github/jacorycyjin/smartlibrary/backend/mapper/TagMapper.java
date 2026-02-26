package io.github.jacorycyjin.smartlibrary.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 标签 Mapper
 * 
 * @author Jacory
 * @date 2025/01/19
 */
@Mapper
public interface TagMapper {

    /**
     * 根据资源ID列表批量查询标签信息
     * 
     * @param resourceIds 资源业务ID列表
     * @return 标签信息列表（包含 resourceId, tagId, tagName, tagType, weight）
     */
    List<Map<String, Object>> selectTagsByResourceIds(@Param("resourceIds") List<String> resourceIds);

    /**
     * 查询所有标签
     * 
     * @return 标签列表
     */
    List<Map<String, Object>> selectAll();
}
