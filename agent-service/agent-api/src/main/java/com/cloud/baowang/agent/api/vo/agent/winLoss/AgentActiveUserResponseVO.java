package com.cloud.baowang.agent.api.vo.agent.winLoss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/10/22 22:41
 * @description:
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentActiveUserResponseVO {
    @Schema(title = "代理ID")
    private String agentId;
    @Schema(title = "有效活跃会员数")
    private Integer activeNumber = 0;
    @Schema(title = "有效新增会员数")
    private Integer newValidNumber = 0;
    @Schema(title = "总有效投注")
    private BigDecimal totalValidAmount = BigDecimal.ZERO;
    @Schema(title = "总输赢")
    private BigDecimal totalWinLoss  = BigDecimal.ZERO;
    @Schema(title = "有效新增会员id列表")
    private List<String> validUserIdList;
}
