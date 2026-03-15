package com.cloud.baowang.report.api.vo.rechagerwithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@Schema(title = "代理下会员存提总计 入参")
public class ReportUserRechargeAgentReqVO {
    @Schema(description = "站点code")
    private String siteCode;
    @Schema(description = "搜索币种")
    private String currency;
    @Schema(description = "代理id")
    private List<String> agentIds;
    @Schema(description = "开始时间")
    private Long startTime;
    @Schema(description = "结束时间")
    private Long endTime;
}
