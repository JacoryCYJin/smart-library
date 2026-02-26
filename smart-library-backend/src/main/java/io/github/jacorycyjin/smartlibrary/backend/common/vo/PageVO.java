package io.github.jacorycyjin.smartlibrary.backend.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageDTO;
import io.github.jacorycyjin.smartlibrary.backend.common.util.BaseSerial;

import java.util.List;

/**
 * @author L.Sheep
 * @date 2024/12/16
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PageVO<T> extends BaseSerial{

    /**
     * 总数据量
     */
    private Integer totalCount;

    /**
     * 总页数
     */
    private Integer pageCount;

    /**
     * 对象列表
     */
    private List<T> list;

    public PageVO(PageDTO<?> page, List<T> list) {
        this.totalCount = page.getTotalCount();
        this.pageCount = page.getPageCount();
        this.list = list;
    }

}
