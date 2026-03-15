package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/10/22 15:59
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AgentDepositWithFeeVO {
/*    *//**
     * 方式手续费
     *//*
    private BigDecimal wayFeeAmount;*/

    /**
     * 结算手续费
     */
    private BigDecimal settleFeeAmount;

    /**
     * 货币代码
     */
    private String currencyCode;

    private Integer type;
}
