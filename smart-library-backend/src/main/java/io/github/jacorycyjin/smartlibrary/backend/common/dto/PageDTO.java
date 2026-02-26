package io.github.jacorycyjin.smartlibrary.backend.common.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import io.github.jacorycyjin.smartlibrary.backend.common.util.PageUtils;
import io.github.jacorycyjin.smartlibrary.backend.common.util.BaseSerial;


import java.util.List;


@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class PageDTO<T> extends BaseSerial {

    /**
     * 当前页码
     */
    private Integer pageNum;

    /**
     * 总数据量
     */
    private Integer totalCount;

    /**
     * 总页数
     */
    private Integer pageCount;

    /**
     * 每页数据量
     */
    private Integer pageSize;
    /**
     * 数据
     */
    private List<T> list;

    private T total;

    /**
     * @param totalCount 总数据量
     * @param pageSize   每页数据量
     * @param list       数据
     */
    public PageDTO(Integer totalCount, Integer pageSize, List<T> list) {
        this.totalCount = totalCount;
        this.pageSize = pageSize == null ? list.size() : pageSize;
        if (this.totalCount == null) {
            this.totalCount = 0;
        }
        this.pageCount = pageSize == null ? 1 : computeLastPageNumber(this.totalCount, pageSize);
        this.list = list;
    }

    public PageDTO(Integer pageNum, Integer totalCount, Integer pageSize, List<T> list) {
        this(totalCount, pageSize, list);
        this.pageNum = pageNum;
    }

    private int computeLastPageNumber(int totalElements, int pageSize) {
        return PageUtils.computeLastPageNumber(totalElements, pageSize);
    }

}
