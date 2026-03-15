package com.cloud.baowang.user.api.vo.userlabel;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


/**
 * 新增标签VO
 */
@Data
@Schema(description = "新增标签 Req VO")
public class UserLabelAddRequestVO {

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "operator",hidden = true)
    private String operator;

    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "标签名称")
    private String labelName;

    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "标签描述")
    private String labelDescribe;

    @Schema(description = "颜色")
    private String color;

    @Schema(description = "状态")
    private String status;
}
