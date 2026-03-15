package com.cloud.baowang.agent.api.vo.withdraw;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(title = "代理取款订单详情返回对象")
@I18nClass
public class AgentWithdrawOrderDetailVO {

    @Schema(description = "订单编码")
    private String orderNo;



    @Schema(description ="取款方式")
    @I18nField
    private String depositWithdrawWay;

    /**
     * 存取款类型CODE
     */
    @Schema(description ="取款类型Code bank_card银行卡 electronic_wallet电子钱包 crypto_currency加密货币 bank_card_transfer银行转账 ")
    private String depositWithdrawTypeCode;

    /**
     * 存取款通道类型(THIRD 三方，OFFLINE 线下）
     */
    @Schema(description ="存款通道类型(THIRD 三方，OFFLINE 线下）")
    private String depositWithdrawChannelType;

    /**
     * 交易币种金额
     */
    @Schema(description ="实际交易金额")
    private BigDecimal tradeCurrencyAmount;

    @Schema(description ="订单金额")
    private BigDecimal applyAmount;

    @Schema(description ="存款汇率")
    private BigDecimal exchangeRate;

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

    @Schema(description ="取款时间")
    private Long createdTime;

    @Schema(description ="更新时间")
    private Long updatedTime;

    @Schema(description ="取款剩余时间 单位秒")
    private Long remindTime;

    @Schema(description ="取款户名")
    private String accountName;

    @Schema(description ="取款账户地址")
    private String accountAddress;
/*
    @Schema(description =" 账户类型（ 银行卡为银行名称，虚拟币为币种 ）")
    private String accountType;

    @Schema(description =" 账户分支（银行卡为开户行，虚拟币为链协议 如ERC20 TRC20)")
    private String accountBranch;

    @Schema(description ="二维码图片,支付宝支付才会返回，U 二位码需前端更具地址生成")
    private String qrCodePicUrl;*/

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
}
