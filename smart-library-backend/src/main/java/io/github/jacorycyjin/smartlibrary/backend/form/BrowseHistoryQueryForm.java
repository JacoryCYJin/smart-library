package io.github.jacorycyjin.smartlibrary.backend.form;

import io.github.jacorycyjin.smartlibrary.backend.common.form.PageQueryForm;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 浏览历史查询表单
 * 
 * @author Jacory
 * @date 2026/03/25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class BrowseHistoryQueryForm extends PageQueryForm {
    // 目前只需要分页参数，后续可扩展其他查询条件
}
