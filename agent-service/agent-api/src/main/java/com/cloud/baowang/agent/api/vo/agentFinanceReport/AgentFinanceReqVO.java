package com.cloud.baowang.agent.api.vo.agentFinanceReport;

import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "代理财务报表")
public class AgentFinanceReqVO {
    @Schema(description = "代理id", hidden = true)
    private String agentId;
    @Schema(description = "站点", hidden = true)
    private String siteCode;
    @Schema(description =  "代理账号", hidden = true)
    private String agentAccount;
    @Schema(description = "币种", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currencyCode = CommonConstant.PLAT_CURRENCY_CODE;
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "统计-开始时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long startTime;
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "统计-结束时间", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long endTime;
}
