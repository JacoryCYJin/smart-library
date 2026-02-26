package io.github.jacorycyjin.smartlibrary.backend.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.github.jacorycyjin.smartlibrary.backend.common.util.BaseSerial;


@Data
@EqualsAndHashCode(callSuper = false)
public class PageQueryDTO extends BaseSerial {

    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;
    private static final int MAX_PAGE_SIZE = 100;

    /**
     * 每页数据量
     */
    private Integer pageSize;

    /**
     * 页码
     */
    private Integer pageNum;

    /**
     * 分页查询flg
     */
    private Boolean fetchAll = false;

    public boolean isFetchAll() {
        return fetchAll != null && fetchAll;
    }

    /**
     * 分页参数校验
     */
    public void validPageParam() {
        if (pageSize == null || pageSize < 1) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        // 限制最大分页大小，防止恶意请求
        if (pageSize > MAX_PAGE_SIZE) {
            pageSize = MAX_PAGE_SIZE;
        }
        if (pageNum == null || pageNum < 1) {
            pageNum = DEFAULT_PAGE_NUM;
        }
    }

}
