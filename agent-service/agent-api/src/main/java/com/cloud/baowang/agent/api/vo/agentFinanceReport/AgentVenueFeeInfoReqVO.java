package com.cloud.baowang.agent.api.vo.agentFinanceReport;


import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
@Schema(description = "代理财务报表-平台费详情入参VO")
public class AgentVenueFeeInfoReqVO {
    @Schema(description = "代理账号", hidden = true)
    private String agentAccount;
    @Schema(description = "代理id", hidden = true)
    private String agentId;
    @Schema(description = "站点编码", hidden = true)
    private String siteCode;
    @Schema(description = "币种")
    private String currencyCode = CommonConstant.PLAT_CURRENCY_CODE;
    @NotNull
    @Schema(description = "统计-开始时间")
    private Long startTime;
    @NotNull
    @Schema(description = "统计-结束时间")
    private Long endTime;
    @Schema(description = "是否个人 1 个人 2团队", required = true)
    private Integer self = CommonConstant.business_one;
}
