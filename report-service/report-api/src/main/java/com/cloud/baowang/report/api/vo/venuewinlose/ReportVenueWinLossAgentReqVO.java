package com.cloud.baowang.report.api.vo.venuewinlose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代理下场馆费计算入参")
public class ReportVenueWinLossAgentReqVO {
    @Schema(description = "站点code")
    private String siteCode;
    @Schema(description = "代理id")
    private List<String> agentIds;
    @Schema(description = "开始时间")
    private Long startTime;
    @Schema(description = "结束时间")
    private Long endTime;
    @Schema(description = "币种")
    private String currency;
}
