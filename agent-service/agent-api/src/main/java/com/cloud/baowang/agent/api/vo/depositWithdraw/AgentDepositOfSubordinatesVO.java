package com.cloud.baowang.agent.api.vo.depositWithdraw;


import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.enums.ResultCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "代理代会员存款请求对象")
public class AgentDepositOfSubordinatesVO {
    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(title = "代存钱包（1 佣金代存 2 额度代存）")
    @NotBlank(message = ConstantsCode.AGENT_H5_WALLET_NOT_EMPTY)
    private String depositSubordinatesType;

    @Schema(title = "下级会员")
    @NotBlank(message = ConstantsCode.USER_ACCOUNT_NOT_EMPTY)
    private String userAccount;

    @Schema(title = "代存金额")
    @NotNull(message = ConstantsCode.AGENT_H5_PASSWORD_AMOUNT_NOT_EMPTY)
    private BigDecimal depositAmount;

    @Schema(title = "支付密码")
    @NotBlank(message = ConstantsCode.AGENT_H5_PASSWORD_NOT_EMPTY)
    private String payPassword;

    @Schema(title = "备注")
    @NotBlank(message = ConstantsCode.REMARK_NOT_BLANK)
    private String remark;

    @Schema(title = "流水倍数")
    @NotNull(message = ConstantsCode.ACTIVITY_BET_IS_NULL_DESC)
    private BigDecimal runningWaterMultiple;

    private String agentAccount;

    private Integer deviceType;
}
