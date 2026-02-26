package io.github.jacorycyjin.smartlibrary.backend.service;

import io.github.jacorycyjin.smartlibrary.backend.vo.AuthorDetailVO;

/**
 * 作者服务接口
 * 
 * @author Jacory
 * @date 2025/01/27
 */
public interface AuthorService {

    /**
     * 获取作者详情（包含作品列表）
     * 
     * @param authorId 作者业务ID
     * @return 作者详情 VO
     */
    AuthorDetailVO getAuthorDetail(String authorId);

    /**
     * 根据资源ID和排序查询作者ID
     * 
     * @param resourceId 资源ID
     * @param sort 排序（1=第一作者，2=第二作者...）
     * @return 作者ID
     */
    String getAuthorIdByResourceAndSort(String resourceId, Integer sort);
}
