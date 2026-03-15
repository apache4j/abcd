package com.cloud.baowang.user.api.vo.userlabel;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: 阿虹
 */
@Data
@Schema(description = "编辑标签 Request")
public class UserLabelEditRequestVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "operator",hidden = true)
    private String operator;
    @Schema(description = "id")
    @NotNull(message = "id不能为空")
    private String id;

    @NotEmpty(message = "标签名称不能为空")
    @Schema(description = "标签名称")
    private String labelName;

    @NotEmpty(message = "标签描述不能为空")
    @Schema(description = "标签描述")
    private String labelDescribe;

    @Schema(description = "颜色")
    private String color;

    @NotEmpty(message = "状态不能为空")
    @Schema(description = "状态")
    private String status;
}
