package com.cloud.baowang.report.api.vo.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @className: AgentUserVenueLisParam
 * @author: wade
 * @description: 代理投注,输赢前3
 * @date: 2024/5/31 20:53
 */
@Data
@NoArgsConstructor
@Schema(description = "代理投注,输赢前3")
public class ReportAgentUserVenueLisParam {

    private String siteCode;
    private String timeZone;
    /**
     * 代理账号ids
     */
    private List<String> agentIds;

    @Schema(title = "开始时间")
    private Long startTime;

    @Schema(title = "结束时间")
    private Long endTime;


}
