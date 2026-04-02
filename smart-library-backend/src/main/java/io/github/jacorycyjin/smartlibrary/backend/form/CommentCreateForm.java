package io.github.jacorycyjin.smartlibrary.backend.form;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 创建评论表单
 * 
 * @author Jacory
 * @date 2025/01/21
 */
@Data
public class CommentCreateForm {

    /**
     * 资源ID
     */
    @NotBlank(message = "资源ID不能为空")
    private String resourceId;

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(min = 1, max = 1000, message = "评论内容长度必须在1-1000字符之间")
    private String content;

    /**
     * 评分（0-10）
     */
    @DecimalMin(value = "0.0", message = "评分最低为0分")
    @DecimalMax(value = "10.0", message = "评分最高为10分")
    private BigDecimal score;
}
