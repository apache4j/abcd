package com.cloud.baowang.wallet.api.vo.agent;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@I18nClass
@Schema(description ="代理下会员操作总计通用vo")
public class WalletAgentSubLineResVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description ="代理账号")
    private String agentAccount;
    @Schema(description ="金额")
    private BigDecimal amount;
    @Schema(description ="手续费/平台费")
    private BigDecimal feeAmount;
    @Schema(description ="场馆费率")
    private BigDecimal venueRate;
    @Schema(description = "场馆code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;
    @Schema(description ="场馆名称")
    private String venueCodeText;
    @Schema(description ="流水")
    private BigDecimal validBetAmount;
    @Schema(description ="币种")
    private String currency;
}
