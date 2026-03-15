package com.cloud.baowang.system.api.vo.areaLimit;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "区域限制分页返回vo")
public class AreaLimitManagerStatusChangeReqVO {
    @NotBlank
    @Schema(description = "id")
    private String id;
    @NotNull
    @Schema(description = "状态 1开0关")
    private Integer status;
    @Schema(description = "操作人", hidden = true)
    private String operator;
}
