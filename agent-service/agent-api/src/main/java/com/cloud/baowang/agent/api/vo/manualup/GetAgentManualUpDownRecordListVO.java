/**
 * @(#)AgentManualUpDownVO.java, 11月 02, 2023.
 * <p>
 * Copyright 2023 pingge.com. All rights reserved.
 * PINGHANG.COM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cloud.baowang.agent.api.vo.manualup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author kimi
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetAgentManualUpDownRecordListVO {

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "调整方式:1-加额，2-减额")
    private Integer adjustWay;

    @Schema(title = "调整金额")
    private BigDecimal adjustAmount;

    @Schema(title = "钱包类型 1额度钱包 2佣金钱包")
    private Integer walletType;

     @Schema(title ="代理提款（后台）是否大额出款;0-否，1-是")
    private String isBigMoney;
}
