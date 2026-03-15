package com.cloud.baowang.system.api.vo.risk;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "风险控制层级接参对象")
public class RiskLevelReqVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(title = "风控类型; 字典code:risk_control_type")
    private String riskControlType;

    @Schema(title = "风控层级code")
    private Long[] riskControlLevelCode;

    @Schema(title = "创建人name")
    private String creatorName;

    @Schema(title = "最近操作人name")
    private String updaterName;

}
