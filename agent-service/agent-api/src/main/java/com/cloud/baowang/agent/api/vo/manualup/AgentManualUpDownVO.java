/**
 * @(#)AgentManualUpDownVO.java, 11月 02, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.agent.api.vo.manualup;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <h2></h2>
 * @author wayne
 * date 2023/11/2
 */

@Data
public class AgentManualUpDownVO {
    private Integer adjustWay;
    private String agentId;
    private Integer countTime;
    private BigDecimal amount;
    private BigDecimal depositeAmount;
    private BigDecimal withdrawAmount;
}
