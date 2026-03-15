package com.cloud.baowang.agent.api.vo.agent.winLoss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/10/22 22:39
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentActiveUserReqVO {
    @Schema( description = "代理Id列表")
    private List<String> agentIds;

    @Schema( description = "开始时间")
    private Long startTime;

    @Schema( description = "结束时间")
    private Long endTime;

    private String siteCode;

    private String planCode;
}
