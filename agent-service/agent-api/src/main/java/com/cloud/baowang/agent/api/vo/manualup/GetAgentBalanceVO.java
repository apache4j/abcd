package com.cloud.baowang.agent.api.vo.manualup;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(title = "查询 返回")
public class GetAgentBalanceVO {
    @Schema(description = "代理id")
    private String agentId;
    @Schema(description = "代理名称")
    private String agentName;
    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(description = "钱包余额")
    private String agentBalance;

}
