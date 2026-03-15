package com.cloud.baowang.agent.api.vo.withdraw;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Schema(title = "代理端取款记录返回对象")
@I18nClass
public class ClientAgentWithdrawRecordResponseVO {

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "交易时间")
    private Long tradeTime;


    @Schema(description = "交易金额")
    private BigDecimal tradeAmount;

    @Schema(description = "到账金额")
    private BigDecimal arriveAmount;

    @Schema(description = "交易方式类型  manual_down:人工减额，" +
            "bank_card_withdraw:银行卡取款，electronic_wallet_withdraw:电子钱包取款，" +
            "crypto_currency_withdraw:加密货币取款")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.TRADE_WAY_TYPE )
    private String tradeWayType;

    @Schema(description = "交易方式类型名称")
    private String tradeWayTypeText;


    @Schema(description = "交易状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_CUSTOMER_STATUS )
    private String tradeStatus;

    @Schema(description = "交易状态名称")
    private String tradeStatusText;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "交易账户")
    private String tradeAccount;

    @Schema(description = "交易协议")
    private String tradeNetWorkType;


    @Schema(description = "币种")
    private String currencyCode;

}
