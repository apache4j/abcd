package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: qiqi
 */
@Data
@I18nClass
@Schema(title = "会员提款审核列表返回对象")
public class UserWithdrawReviewPageResVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "订单号")
    private String orderNo;
    @Schema(description = "会员id")
    private String userId;
    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "会员标签")
    private String userLabel;
    @Schema(description = "会员标签数组")
    private List<UserWithdrawLabelVO> userLabelList;


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

    @Schema(description = "提款类型code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.WITHDRAW_TYPE)
    private String depositWithdrawTypeCode;

    @Schema(description = "提款类型名称")
    private String depositWithdrawTypeCodeText;

    @Schema(description = "提款方式")
    private String depositWithdrawWayId;

    @Schema(description = "提款方式")
    @I18nField
    private String depositWithdrawWay;

    @Schema(description = "收款账户")
    private String depositWithdrawAddress;

    @Schema(description = "收款账户标识  0 首次使用 1一直该账号使用 2 多个账号使用")
    private String addressColor;

    @Schema(description = "提款IP")
    private String applyIp;

    @Schema(description = "提款IP风控层级")
    private String applyIpRiskLevel;

    @Schema(description = "提款币种")
    private String currencyCode;


    @Schema(description = "提款币种金额")
    private BigDecimal tradeCurrencyAmount;

    @Schema(description = "汇率")
    private BigDecimal exchangeRate;

    @Schema(description = "申请时间")
    private Long createdTime;

    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_STATUS)
    private String status;

    @Schema(description = "订单状态名称")
    private String statusText;

    @Schema(description = "审核操作")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_WITHDRAW_REVIEW_OPERATION)
    private Integer reviewOperation;

    @Schema(description = "审核操作")
    private String reviewOperationText;


    @Schema(description = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;


    @Schema(description = "审核员/锁单人")
    private String locker;


    @Schema(description = "锁单人是否当前登录人 0否 1是")
    private String isLocker;

    @Schema(description = "之前审核人是否当前登录人 0否 1是")
    private String isReviewer;

    @Schema(description = "三方消息状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_PAY_PROCESS_STATUS)
    private String payProcessStatus;

    @Schema(description = "三方消息状态名称")
    private String payProcessStatusText;


    @Schema(description = "账户类型（ 银行卡为银行名称，虚拟币为币种）")
    private String accountType;


    @Schema(description = "账户分支（银行卡为开户行，虚拟币为链协议 如ERC20 TRC20)")
    private String accountBranch;

    @Schema(description = "账户名/持卡人姓名")
    private String depositWithdrawName;

    @Schema(description = "设备号")
    private String deviceNo;

    @Schema(description = "CPF")
    private String cpf;

}
