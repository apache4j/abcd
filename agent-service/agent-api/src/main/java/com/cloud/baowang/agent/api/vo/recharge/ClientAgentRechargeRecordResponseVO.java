package com.cloud.baowang.agent.api.vo.recharge;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Schema(title = "代理端充值记录返回对象")
@I18nClass
public class ClientAgentRechargeRecordResponseVO {

    @Schema(description = "订单编号")
    private String orderNo;

    @Schema(description = "交易时间")
    private Long tradeTime;


    @Schema(description = "交易金额")
    private BigDecimal tradeAmount;

    @Schema(description = "到账金额")
    private BigDecimal arriveAmount;

    @Schema(description = "交易方式类型  bank_card_recharge:银行卡存款," +
            " electronic_wallet_recharge:电子钱包存款，crypto_currency_recharge:加密货币存款，" +
            "manual_up:人工加额，superior_transfer:上级转入")
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

    /**
     * 收款 姓名/电子钱包姓名
     */
    @Schema(description =" 收款 姓名/电子钱包姓名")
    private String recvUserName;

    /**
     * 收款 银行编码
     */
    @Schema(description ="收款 银行编码")
    private String recvBankCode;
    /**
     * 收款银行名称
     */
    @Schema(description ="收款银行名称")
    private String recvBankName;
    /**
     * 收款开户行
     */
    @Schema(description ="收款开户行")
    private String recvBankBranch;

    /**
     * 收款电子钱包账户
     */
    @Schema(description ="收款电子钱包账户")
    private String recvBankAccount;

    /**
     * 收款码
     */
    @Schema(description ="收款码")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String recvQrCode;

    /**
     * 收款码URL
     */
    @Schema(description ="收款码URL")
    private String recvQrCodeFileUrl;



}
