package com.cloud.baowang.wallet.api.vo.userwallet;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.AppBigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "会员交易记录返回对象")
@I18nClass
public class UserTradeRecordResponseVO {


    @Schema(description = "交易类型 1存款 2取款 3平台币兑换")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.TRADE_TYPE )
    private String tradeType;

    @Schema(description = "交易类型名称")
    private String tradeTypeText;

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "交易时间")
    private Long tradeTime;


    @Schema(description = "交易金额")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal tradeAmount;

    @Schema(description = "到账金额")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal arriveAmount;

    @Schema(description = "交易类型  bank_card_recharge:银行卡存款," +
            " electronic_wallet_recharge:电子钱包存款，crypto_currency_recharge:加密货币存款，" +
            "manual_up:人工加额，superior_transfer:上级转入，manual_down:人工减额，" +
            "platform_transfer:平台币兑换，bank_card_withdraw:银行卡取款，electronic_wallet_withdraw:电子钱包取款，" +
            "crypto_currency_withdraw:加密货币取款")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.TRADE_WAY_TYPE )
    private String tradeWayType;

    @Schema(description = "交易类型名称")
    private String tradeWayTypeText;


    @Schema(description = "交易方式名称")
    @I18nField
    private String tradeWay;

    @Schema(description = "交易方式图片")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String tradeWayIcon;

    @Schema(description = "交易方式图片全路径")
    private String tradeWayIconFileUrl;

    @Schema(description = "交易状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_CUSTOMER_STATUS )
    private String tradeStatus;

    @Schema(description = "交易状态名称")
    private String tradeStatusText;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;



}
