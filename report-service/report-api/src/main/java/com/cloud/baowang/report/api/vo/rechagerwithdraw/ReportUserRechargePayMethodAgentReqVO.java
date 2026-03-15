package com.cloud.baowang.report.api.vo.rechagerwithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@Accessors(chain = true)
@Schema(title = "代理下支付方式会员存提总计 入参")
public class ReportUserRechargePayMethodAgentReqVO {
    @Schema(description = "站点code")
    private String siteCode;
    @Schema(description = "代理id")
    private List<String> agentIds;
    @Schema(description = "开始时间")
    private Long startTime;
    @Schema(description = "结束时间")
    private Long endTime;
    @Schema(description = "存款1 取款2")
    private Integer type;
    @Schema(description = "币种")
    private String currency;
}
