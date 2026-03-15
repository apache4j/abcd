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
public class AgentDepositWithResVO {
    /**
     * 实际到账金额
     */
    private BigDecimal arriveAmount;

    /**
     * 存取款方式id
     */
    private String depositWithdrawWayId;
}
