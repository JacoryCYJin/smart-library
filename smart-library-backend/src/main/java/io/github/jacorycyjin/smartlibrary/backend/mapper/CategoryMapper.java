package io.github.jacorycyjin.smartlibrary.backend.mapper;

import io.github.jacorycyjin.smartlibrary.backend.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;
import java.util.Map;

/**
 * 分类 Mapper
 * 
 * @author Jacory
 * @date 2025/12/31
 */
@Mapper
public interface CategoryMapper {

    /**
     * 通用查询分类方法（支持多条件动态查询）
     * 
     * @param params 查询参数 Map
     *               - categoryId: 单个分类业务ID
     *               - categoryIds: 分类业务ID列表
     *               - parentId: 父分类ID
     *               - level: 层级
     *               - deleted: 是否删除（默认0）
     *               - limit: 查询数量限制
     * @return 分类列表
     */
    List<Category> searchCategories(Map<String, Object> params);

    /**
     * 根据资源ID查询关联的分类（仅一级分类）
     * 
     * @param resourceId 资源业务ID
     * @return 分类实体列表
     */
    List<Category> selectCategoriesByResourceId(@Param("resourceId") String resourceId);

    /**
     * 根据资源ID查询完整分类层级路径
     * 
     * @param resourceId 资源业务ID
     * @return 分类实体列表（包含所有祖先节点）
     */
    List<Category> selectCategoryPathsByResourceId(@Param("resourceId") String resourceId);

    /**
     * 查询指定分类及其所有子分类的ID列表（递归）
     * 
     * @param categoryId 父分类业务ID
     * @return 分类ID列表（包含自身）
     */
    List<String> selectDescendantIds(@Param("categoryId") String categoryId);
}
