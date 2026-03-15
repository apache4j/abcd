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
@Schema(title = "风险控制层级新增对象")
public class RiskLevelEditVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "operator", hidden = true)
    private String operator;
    @Schema(title = "id")
    private String id;
    @Schema(title = "风控类型")
    private String riskControlType;
    @Schema(title = "风控层级")
    private String riskControlLevel;
    @Schema(title = "风控层级描述")
    private String riskControlLevelDescribe;
}
