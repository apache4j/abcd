package com.cloud.baowang.wallet.api.vo.userCoin;


import com.cloud.baowang.common.core.serializer.AppBigDecimalJsonSerializer;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(description = "会员平台币余额信息响应")
public class UserPlatformBalanceRespVO {
    @Schema(description = "会员ID")
    private String userAccount;
    @Schema(description = "站点编码",hidden = true)
    private String siteCode;
    @Schema(description = "平台币可用金额")
    private BigDecimal platAvailableAmount;
    @Schema(description = "平台币币种")
    private String platCurrency;
    @Schema(description = "转换汇率")
    private BigDecimal transferRate;
    @Schema(description = "用户可用金额")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal userAvailableAmount;
    @Schema(description = "用户主货币")
    private String userCurrencyCode;

}
