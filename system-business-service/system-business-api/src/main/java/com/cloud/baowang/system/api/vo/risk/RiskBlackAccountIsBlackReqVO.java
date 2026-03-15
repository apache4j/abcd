package com.cloud.baowang.system.api.vo.risk;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RiskBlackAccountIsBlackReqVO {
    @Schema(title = "账号")
    private String riskControlAccount;
    @Schema(title = "账号名称")
    private String riskControlAccountName;
    /**
     *
     * {@link com.cloud.baowang.wallet.api.enums.wallet.WithdrawTypeEnum}
     * 对应risk
     * {@link com.cloud.baowang.system.api.enums.RiskBlackTypeEnum}
     */
    @Schema(title = "风控类型code")
    private String riskControlTypeCode;

    @Schema(description = "siteCode")
    private String siteCode;
}
