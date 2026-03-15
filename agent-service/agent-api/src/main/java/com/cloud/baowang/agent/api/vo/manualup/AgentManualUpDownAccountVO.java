package com.cloud.baowang.agent.api.vo.manualup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Schema(title = "代理资金调整加额 账号请求对象")
public class AgentManualUpDownAccountVO {

    /**
     * 代理账号
     */
    @Schema(description = "代理账号")
    private String agentAccount;

    /**
     * 调整金额
     */
    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;


}
