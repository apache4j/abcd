package com.cloud.baowang.agent.api.vo.agent.winLoss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/10/22 22:39
 * @description:
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class AgentActiveNumberReqVO {

    @Schema( description = "需要查询的代理id集合")
    private List<String> agentIdList;

    @Schema( description = "代理Id")
    private String agentId;

    @Schema( description = "开始时间")
    private Long startTime;

    @Schema( description = "结束时间")
    private Long endTime;

    @Schema( description = "是否只查询直属会员 是 true 否 false")
    private Boolean isDirect = false;

    private String siteCode;
}
