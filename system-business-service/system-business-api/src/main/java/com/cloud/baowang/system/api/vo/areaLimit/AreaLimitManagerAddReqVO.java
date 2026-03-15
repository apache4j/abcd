package com.cloud.baowang.system.api.vo.areaLimit;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AreaLimitManagerAddReqVO {
    @NotBlank
    @Schema(description = "名称")
    private String name;
    @NotNull
    @Schema(description = "类型 1:ip 2:国家")
    private Integer type;
    @Schema(description = "国家code")
    private String areaCode;
    @Schema(description = "备注")
    private String remark;
    @Schema(description = "操作人", hidden = true)
    private String operator;
}
