package io.github.jacorycyjin.smartlibrary.backend.common.form;

import lombok.Data;
import lombok.EqualsAndHashCode;
import io.github.jacorycyjin.smartlibrary.backend.common.util.BaseSerial;
import io.github.jacorycyjin.smartlibrary.backend.common.dto.PageQueryDTO;

/**
 * 分页查询表单
 * 接收前端驼峰参数 (CamelCase)
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class PageQueryForm extends BaseSerial {
    
    /**
     * 每页数据量
     * 前端传: "pageSize": 10
     */
    private Integer pageSize;

    /**
     * 页码
     * 前端传: "pageNum": 1
     */
    private Integer pageNum;

    /**
     * 是否查询所有
     * 前端传: "fetchAll": false
     */
    private Boolean fetchAll = false;

    /**
     * 转换为业务层使用的 DTO
     */
    public PageQueryDTO toPageQueryDTO() {
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPageNum(this.pageNum);
        dto.setPageSize(this.pageSize);
        dto.setFetchAll(this.fetchAll);
        
        // 执行 DTO 内部的校验逻辑（如设置默认值、最大限制等）
        dto.validPageParam(); 
        return dto;
    }
}