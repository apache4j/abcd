package com.cloud.baowang.wallet.api.vo.userCoin;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(description = "会员平台币钱包余额信息对象")
public class UserPlatformCoinWalletVO {


    @Schema(description = "会员ID")
    private String userAccount;

    @Schema(description = "站点编码")
    private String siteCode;

    @Schema(description = "总金额")
    private BigDecimal totalAmount;

    @Schema(description = "总金额")
    private BigDecimal centerTotalAmount;

    @Schema(description = "可用金额")
    private BigDecimal centerAmount;

    @Schema(description = "冻结金额")
    private BigDecimal centerFreezeAmount;

    @Schema(description = "平台币币种")
    private String currency;



}
