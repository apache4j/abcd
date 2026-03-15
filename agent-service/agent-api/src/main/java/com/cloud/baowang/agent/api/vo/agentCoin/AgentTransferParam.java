package com.cloud.baowang.agent.api.vo.agentCoin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 21/10/23 12:00 PM
 * @Version : 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description ="代理转账请求入参")
public class AgentTransferParam implements Serializable {

    private String agentId;

    private String agentAccount;

    @Schema(description ="钱包类型 (1佣金钱包 2额度钱包）")
    private Integer walletType;

    @Schema(description ="可用额度")
    private BigDecimal availableAmount;

    @Schema(description ="下级代理")
    @NotEmpty(message = "下级代理不能为空")
    private String underAgentAccount;

   @Schema(description ="转账金额")
    @NotNull(message = "转账金额不能为空")
    private BigDecimal transferAmount;

   @Schema(description ="支付密码")
    @NotEmpty(message = "支付密码不能为空")
    private String payPassword;

   @Schema(description ="备注")
    private String remark;

   @Schema(description = "是否是支付佣金 true 是 false 否", hidden = true)
    private Boolean isCommission;

   @Schema(description ="佣金结算周期")
    private Long reportDay;

   private String siteCode;

}
