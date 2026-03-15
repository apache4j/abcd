package com.cloud.baowang.wallet.api.vo.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * @author qiqi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "活动优惠信息")
public class WalletUserPromotionsVO {

    @Schema(description ="已领取的平台币总额")
    private BigDecimal receivedWtcAmount;

    @Schema(description ="已领取的主货币总额")
    private BigDecimal receivedMainCurrencyAmount;

    @Schema(description ="已转化为主货币总额")
    private BigDecimal wtcToMainCurrencyAmount;

    @Schema(description ="已使用的优惠总额")
    private BigDecimal usedDiscountAmount;



    @Schema(description = "主货币币种")
    private String currency;

    @Schema(description = "平台币币种")
    private String platformCurrency;

    public BigDecimal getReceivedWtcAmount() {
        return Optional.ofNullable(receivedWtcAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getReceivedMainCurrencyAmount() {
        return Optional.ofNullable(receivedMainCurrencyAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getWtcToMainCurrencyAmount() {
        return Optional.ofNullable(wtcToMainCurrencyAmount).orElse(BigDecimal.ZERO);
    }

    public BigDecimal getUsedDiscountAmount() {
        return Optional.ofNullable(usedDiscountAmount).orElse(BigDecimal.ZERO);
    }






}
