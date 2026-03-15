package com.cloud.baowang.user.api.vo.userlabel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


/**
 * @author: 阿虹
 */
@Data
@Schema(description = "删除标签 Request")
public class UserLabelDelRequestVO {
    @Schema(description = "id")
    @NotNull(message = "id不能为空")
    private String id;

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "operator", hidden = true)
    private String operator;
}
