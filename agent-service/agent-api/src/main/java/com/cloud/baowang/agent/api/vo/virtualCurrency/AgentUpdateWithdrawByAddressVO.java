package com.cloud.baowang.agent.api.vo.virtualCurrency;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: qiqi
 */
@Data
@Schema(title = "更新 代理提款信息")
public class AgentUpdateWithdrawByAddressVO {

    @Schema(description =  "虚拟币账号地址")
    private String virtualCurrencyAddress;

    @Schema(description =  "代理是否提款成功")
    private Boolean agentWithdrawSuccess;

    @Schema(description =  "代理是否提款被拒")
    private Boolean agentWithdrawFail;

    @Schema(description =  "代理提款金额")
    private BigDecimal agentWithdrawSumAmount;

    @Schema(description =  "操作人id")
    private String adminId;

    @Schema(description =  "操作人账号")
    private String adminName;
}
