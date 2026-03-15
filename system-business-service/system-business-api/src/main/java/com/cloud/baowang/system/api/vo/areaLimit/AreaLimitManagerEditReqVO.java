package com.cloud.baowang.system.api.vo.areaLimit;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "区域限制分页返回vo")
public class AreaLimitManagerEditReqVO {
    @NotBlank
    @Schema(description = "id")
    private String id;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "类型")
    private Integer type;
    @Schema(description = "code")
    private String areaCode;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "操作人", hidden = true)
    private String operator;

}
