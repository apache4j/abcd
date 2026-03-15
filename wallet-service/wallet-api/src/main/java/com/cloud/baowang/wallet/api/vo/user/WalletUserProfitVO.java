package com.cloud.baowang.wallet.api.vo.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "盈利信息")
public class WalletUserProfitVO {


    @Schema(description ="净输赢")
    private BigDecimal profitAndLoss;

    @Schema(description ="风控调整")
    private BigDecimal riskAdjustAmount;

    @Schema(description ="其他调整")
    private BigDecimal otherAdjustAmount;

    @Schema(description ="平台币调整金额")
    private BigDecimal platAdjustAmount;


    @Schema(description = "主货币币种")
    private String currency;

    @Schema(description = "平台币币种")
    private String platformCurrency;


}
