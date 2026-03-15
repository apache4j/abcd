package com.cloud.baowang.agent.api.vo.agentCoin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 19/10/23 7:38 PM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description ="代理转账查询返回对象")
public class AgentTransferVO implements Serializable {

    @Schema(description ="额度钱包余额")
    private BigDecimal quotaCoinBalance;

    @Schema(description ="佣金钱包余额")
    private BigDecimal commissionCoinBalance;

    @Schema(description ="单笔最低转账金额")
    private BigDecimal minTransAmount;

    @Schema(description ="单笔最高转账金额")
    private BigDecimal maxTransAmount;

    @Schema(description ="当日剩余转账额度")
    private BigDecimal remainingTodayAmount;

    @Schema(description ="是否有支付密码(0:有支付密码,1:无支付密码)")
    private Integer isPayPassword;
}
