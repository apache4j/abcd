package com.cloud.baowang.agent.api.vo.withdraw;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "代理提款详情对象")
@I18nClass
public class AgentWithdrawDetailVO {


    @Schema(description = "订单编码")
    private String orderNo;

    @Schema(description = "交易方式类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.TRADE_WAY_TYPE )
    private String tradeWayType;

    @Schema(description = "交易方式类型名称")
    private String tradeWayTypeText;


    @Schema(description ="手续费")
    private BigDecimal feeAmount;

    @Schema(description ="申请金额")
    private BigDecimal applyAmount;

    /**
     * 交易币种金额
     */
    @Schema(description ="交易金额")
    private BigDecimal tradeCurrencyAmount;

    @Schema(description ="到账金额")
    private BigDecimal arriveAmount;

    @Schema(description ="汇率")
    private BigDecimal exchangeRate;



    @Schema(description = "银行名称")
    private String bankName;

    @Schema(description = "银行编码")
    private String bankCode;

    @Schema(description = "银行卡号")
    private String bankCard;

    @Schema(description = "姓")
    private String surname;

    @Schema(description = "名")
    private String userName;

    @Schema(description = "邮箱")
    private String userEmail;

    @Schema(description = "手机区号")
    private String areaCode;

    @Schema(description = "手机号")
    private String userPhone;

    @Schema(description = "省")
    private String provinceName;

    @Schema(description = "市")
    private String cityName;

    @Schema(description = "IFSC码(印度)")
    private String ifscCode;

    @Schema(description = "CPF")
    private String cpf;

    @Schema(description = "详细地址")
    private String detailAddress;
    @Schema(description = "电子账户")
    private String userAccount;
    @Schema(description = "网络协议")
    private String networkType;

    @Schema(description = "加密货币收款地址")
    private String addressNo;

    @Schema(description = "申请时间")
    private Long cratedTime;

    @Schema(description = "到账时间")
    private Long updatedTime;

    @Schema(description ="客户端状态 0处理中 1成功 2失败 ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_CUSTOMER_STATUS)
    private String customerStatus;

    @Schema(description ="客户端状态名称")
    private String customerStatusText;


    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "平台币汇率")
    private BigDecimal platformExchangeRate;

    @Schema(description = "平台币币种")
    private String platformCurrencyCode;
}
