package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Schema(title = "会员提款审核记录返回对象")
@Data
@I18nClass
public class UserWithdrawReviewRecordVO {

    private String id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_STATUS)
    private String status;

    @Schema(description = "订单状态名称")
    private String statusText;

    @Schema(description = "是否大额提款")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isBigMoney;

    @Schema(description = "是否大额提款名称")
    private String isBigMoneyText;


    @Schema(description = "是否首提")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isFirstOut;

    @Schema(description = "是否首提名称")
    private String isFirstOutText;


    @Schema(description = "提款金额")
    private BigDecimal applyAmount;

    @Schema(description = "提款类型id")
    private String depositWithdrawTypeId;

    @Schema(description = "提款类型")
    private String depositWithdrawTypeCode;

    @Schema(description = "提款方式id")
    private String depositWithdrawWayId;

    @Schema(description = "提款方式i18")
    @I18nField
    private String depositWithdrawWay;

    @Schema(description = "提款通道")
    private String depositWithdrawChannelCode;

    @Schema(description = "通道名称")
    private String depositWithdrawChannelName;

    @Schema(description = "提款币种")
    private String currencyCode;

    @Schema(description = "提款手续费")
    private BigDecimal feeAmount;

    @Schema(description = "提款币种金额")
    private BigDecimal tradeCurrencyAmount;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    /**
     * 账户类型（ 银行卡为银行名称，虚拟币为币种）
     */
    @Schema(description = "账户类型（ 银行卡为银行名称，虚拟币为币种）")
    private String accountType;


    @Schema(description = "账户分支（银行卡为开户行，虚拟币为链协议 如ERC20 TRC20)")
    private String accountBranch;

    /**
     * 存取款地址
     */
    @Schema(description = "账户地址（银行卡位账号，虚拟币为地址")
    private String depositWithdrawAddress;

    /**
     * 存取款用户名
     */
    @Schema(description = "账户名/持卡人姓名")
    private String depositWithdrawName;

    @Schema(description = "出款渠道")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_CHANNEL)
    private String depositWithdrawChannel;

    @Schema(description = "出款渠道名称")
    private String depositWithdrawChannelText;

    @Schema(description = "出款类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.CHANNEL_TYPE)
    private String depositWithdrawChannelType;
    @Schema(description = "出款类型")
    private String depositWithdrawChannelTypeText;

    @Schema(description = "审核人信息")
    private String auditUserInfo;

    @Schema(description = "审核时间信息")
    private String auditTimeInfo;

    @Schema(description = "审核备注信息")
    private String auditRemarkInfo;

    @Schema(description = "审核用时信息")
    private String auditUseTimeInfo;

    @Schema(description = "申请时间")
    private Long createdTime;
    @Schema(description = "提款时间")
    private Long updatedTime;

}
