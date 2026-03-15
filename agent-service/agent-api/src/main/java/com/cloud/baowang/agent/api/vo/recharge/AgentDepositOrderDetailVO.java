package com.cloud.baowang.agent.api.vo.recharge;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(title = "充值订单详情返回对象")
@I18nClass
public class AgentDepositOrderDetailVO {

    @Schema(description = "订单编码")
    private String orderNo;



    @Schema(description ="充值方式")
    @I18nField
    private String depositWithdrawWay;

    /**
     * 存取款类型CODE
     */
    @Schema(description ="充值类型Code bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币 bank_card_transfer银行转账 ")
    private String depositWithdrawTypeCode;

    /**
     * 存取款通道类型(THIRD 三方，OFFLINE 线下）
     */
    @Schema(description ="存款通道类型(THIRD 三方，OFFLINE 线下,SITE_CUSTOM 站点自定义）")
    private String depositWithdrawChannelType;


    @Schema(description ="订单金额")
    private BigDecimal applyAmount;

    /**
     * 交易币种金额
     */
    @Schema(description ="实际交易金额")
    private BigDecimal tradeCurrencyAmount;

    /**
     * 账变金额
     */
    @Schema(description ="账变金额")
    private BigDecimal arriveAmount;

    @Schema(description ="存款汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "平台币汇率")
    private BigDecimal platformExchangeRate;

    @Schema(description = "平台币币种")
    private String platformCurrencyCode;

    /**
     * 手续费率
     */
    @Schema(description ="手续费率")
    private BigDecimal feeRate;

    /**
     * 手续费
     */
    @Schema(description ="手续费")
    private BigDecimal feeAmount;


    @Schema(description ="客户端状态 0处理中 1成功 2失败 ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_CUSTOMER_STATUS)
    private String customerStatus;

    @Schema(description ="客户端状态名称")
    private String customerStatusText;

    @Schema(description ="充值时间")
    private Long createdTime;

    @Schema(description ="更新时间")
    private Long updatedTime;

    @Schema(description ="充值剩余时间 单位秒")
    private Long remindTime;

    @Schema(description ="充值户名")
    private String accountName;


    @Schema(description ="凭证上传标志  0 未上传 1 已上传")
    private Integer  voucherFlag;

    @Schema(description = "资金流水凭证图片 多个逗号隔开")
    private String cashFlowFile;

    @Schema(description = "资金流水凭证数组")
    private List<String> cashFlowFileList;

    @Schema(description ="三方支付URL路径")
    private String thirdPayUrl;

    @Schema(description ="催单标志(0未催单 1已催单)")
    private Integer urgeOrder;

    @Schema(description ="目标币种")
    private String currencyCode;

    @Schema(description ="来源币种")
    private String coinCode;


    @Schema(description ="银行帐号/电子钱包地址/虚拟币地址")
    private String accountAddress;
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


    /**
     * 网络协议类型 TRC20 ERC20
     */
    @Schema(description = "网络协议类型 TRC20 ERC20")
    private String networkType;
}
