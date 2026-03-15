package com.cloud.baowang.system.api.vo.areaLimit;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "区域限制详情返回vo")
public class AreaLimitManagerIdReqVO {

    @NotBlank
    @Schema(description = "id")
    private String id;
}
