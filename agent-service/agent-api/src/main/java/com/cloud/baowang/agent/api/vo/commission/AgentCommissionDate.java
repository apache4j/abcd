package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: fangfei
 * @createTime: 2024/11/07 17:59
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "佣金结算时间")
public class AgentCommissionDate {
    @Schema(description = "代理Id")
    private String agentId;
    @Schema(description = "本期结算开始时间")
    private Long currentStartTime;
    @Schema(description = "本期结算结束时间")
    private Long currentEndTime;
    @Schema(description = "上期结算开始时间")
    private Long lastStartTime;
    @Schema(description = "上期结算结束时间")
    private Long lastEndTime;
}
