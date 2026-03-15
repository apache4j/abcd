package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/10/20 14:33
 */
@Data
public class AgentCommissionExpectCalcVO {
    @Schema(description ="开始时间")
    private Long startTime;
    @Schema(description ="结束时间")
    private Long endTime;
    @Schema(description ="时区")
    private String timeZone;
    @Schema(description ="周期 0 日  1 周 2 月")
    private Integer settleCycle;
    @Schema(description ="是否手动  0 否 1 是")
    private Integer isManual;
    @Schema(description ="siteCode")
    private String siteCode;
    @Schema(description ="代理ID")
    private List<String> agentIds;
}
