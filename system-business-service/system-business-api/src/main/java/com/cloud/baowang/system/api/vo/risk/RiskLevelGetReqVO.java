package com.cloud.baowang.system.api.vo.risk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "查询风险控制层级接参对象")
public class RiskLevelGetReqVO {
    @Schema(title = "风控类型")
    private String riskControlType;
    @Schema(title = "风控层级code")
    private String riskControlLevelCode;
}
