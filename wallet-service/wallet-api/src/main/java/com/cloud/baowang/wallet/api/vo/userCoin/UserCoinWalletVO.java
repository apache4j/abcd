package com.cloud.baowang.wallet.api.vo.userCoin;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author qiqi
 */
@Data
@Schema(description = "会员钱包余额信息对象")
public class UserCoinWalletVO {

    private String userId;

    @Schema(description = "会员ID")
    private String userAccount;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "总金额（包含平台币）")
    private BigDecimal totalAmount;

    @Schema(description = "主货币总金额（包含冻结金额）")
    private BigDecimal centerTotalAmount;

    @Schema(description = "主货币可用金额")
    private BigDecimal centerAmount;

    @Schema(description = "主货币冻结金额")
    private BigDecimal centerFreezeAmount;

    @Schema(description = "主货币币种")
    private String currency;

    @Schema(description = "平台币币种")
    private String platformCurrency;

    @Schema(description = "平台币金额")
    private BigDecimal platformAmount;

   /* @Schema(description = "活动彩金总计")
    private BigDecimal alreadyUseAmount;

    @Schema(description = "WTC已兑换成主货币的金额")
    private BigDecimal wtcToMainCurrencyAmount;*/


}
