package com.cloud.baowang.system.api.vo.risk;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "代理会员风控")
@I18nClass
public class RiskUserCountVo {
    @Schema(description = "风控层级")
    private String riskLevel;

    @Schema(description = "当前风控层级下对应会员个数")
    private Long riskUserCount;
}
