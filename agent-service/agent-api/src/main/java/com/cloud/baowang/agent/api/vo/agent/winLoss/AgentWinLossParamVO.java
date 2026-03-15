package com.cloud.baowang.agent.api.vo.agent.winLoss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代理查询下级输赢请求对象")
public class AgentWinLossParamVO {

    private String siteCode;
    @Schema( description = "开始时间")
    private Long startTime;

    @Schema( description = "结束时间")
    private Long endTime;

    private List<String> agentIds;

}
