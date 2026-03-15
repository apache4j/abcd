package com.cloud.baowang.agent.api.vo.agentFinanceReport;


import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "代理财务报表-存提手续费详情查询入参vo")
public class AgentDepositWithdrawFeeInfoReqVO {
    @Schema(description =  "代理id", hidden = true)
    private String agentId;
    @Schema(description =  "站点", hidden = true)
    private String siteCode;
    @Schema(description =  "代理账号", hidden = true)
    private String agentAccount;
    @Schema(description = "币种")
    private String currencyCode = CommonConstant.PLAT_CURRENCY_CODE;
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "统计-开始时间", required = true)
    private Long startTime;
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "统计-结束时间", required = true)
    private Long endTime;
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "存取款 1存款 2取款", required = true)
    private Integer type;
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "是否个人 1 个人 2团队", required = true)
    private Integer self;

}
