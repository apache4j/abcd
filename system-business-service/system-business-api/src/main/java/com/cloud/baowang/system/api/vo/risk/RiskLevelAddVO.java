package com.cloud.baowang.system.api.vo.risk;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "风险控制层级新增对象")
public class RiskLevelAddVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "operator", hidden = true)
    private String operator;
    @Schema(title = "风控类型")
    @NotBlank(message = "风控类型不能为空")
    private String riskControlType;
    @Schema(title = "风控层级")
    @NotBlank(message = "风控层级不能为空")
    private String riskControlLevel;
    @Schema(title = "风控层级描述")
    private String riskControlLevelDescribe;
}
