package com.cloud.baowang.agent.api.vo.winloss;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * @Author : 小智
 * @Date : 25/10/23 2:21 PM
 * @Version : 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代理盈亏具体返回明细对象")
public class AgentUserWinLossVO implements Serializable {

    @Schema(description ="代理账号名称")
    private String agentAccount;

    @Schema(description ="直属会员标识(0:是,1:不是)")
    private Integer directFlag;

    @Schema(description ="是否是我(0:是,1:不是)")
    private Integer isMe;

    @Schema(description ="是否有下级标识(0:有,1:没有)")
    private Integer underFlag;

    @Schema(description ="会员总投注")
    private BigDecimal totalBetAmount;

    @Schema(description ="会员游戏盈亏")
    private BigDecimal totalWinLossAmount;

    @Schema(description ="我的团队:冲正净输赢, 我的直属会员:会员总盈亏")
    private BigDecimal totalProfitAndLoss;

    public BigDecimal getTotalBetAmount() {
        return Optional.ofNullable(totalBetAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalWinLossAmount() {
        return Optional.ofNullable(totalWinLossAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getTotalProfitAndLoss() {
        return Optional.ofNullable(totalProfitAndLoss).orElse(BigDecimal.ZERO);
    }
}
