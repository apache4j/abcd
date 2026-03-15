package com.cloud.baowang.play.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @className: getAgentUserVenueListParam
 * @author: wade
 * @description: 代理查询统计
 * @date: 2024/5/31 20:14
 */
@Data
@Schema(title = "代理查询统计")
public class AgentUserVenueListParam {
    @Schema(title = "开始时间")
    private Long startTime;

    @Schema(title = "结束时间")
    private Long endTime;


    @Schema(title = "代理ids")
    List<String> agentIds;

    @Schema(title = "代理账号s")
    List<String> agentAcctIds;

}
