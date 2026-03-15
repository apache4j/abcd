package com.cloud.baowang.agent.api.vo.agentreview.info;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 13/10/23 10:55 PM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "代理团队信息概览返回对象")
public class AgentAllDownUserVO implements Serializable {
    @Schema(description = "主货币")
    private String mainCurrency;
    @Schema(description = "会员ID")
    private Long userId;
    @Schema(description = "首存时间")
    private Long firstDepositTime;
    @Schema(description = "首存金额")
    private BigDecimal firstDepositAmount;
}
