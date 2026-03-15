/**
 * @(#)AgentManualUpDownVO.java, 11月 02, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.agent.api.vo.manualup;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * <h2></h2>
 * @author kimi
 * date 2023/11/2
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAllUpCommissionByAgentAccountsVO {
    private String agentAccount;

    private BigDecimal allUpCommission = BigDecimal.ZERO;
}
