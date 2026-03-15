package com.cloud.baowang.system.api.vo.risk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "风控详情")
public class RiskLevelDetailsVO {

    @Schema(title = "id")
    private String id;

    @Schema(title = "风控类型")
    private String riskControlType;

    @Schema(title = "风控层级")
    private String riskControlLevel;
}
