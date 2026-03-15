package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: qiqi
 */
@Data
@I18nClass
@Schema(title = "代理提款审核列表返回对象")
public class AgentWithdrawReviewPageResVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;


    @Schema(description = "审核员/锁单人")
    private String locker;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "订单号")
    private String orderNo;


    @Schema(description = "提款方式")
    private String depositWithdrawWayId;

    @Schema(description = "提款方式")
    @I18nField
    private String depositWithdrawWay;

    @Schema(description = "收款账户")
    private String depositWithdrawAddress;

    @Schema(description = "收款账户标识  0 首次使用 1一直该账号使用 2 多个账号使用")
    private String addressColor;


    @Schema(description = "是否大额提款")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private String isBigMoney;

    @Schema(description = "是否大额提款名称")
    private String isBigMoneyText;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "申请金额")
    private BigDecimal applyAmount;

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

    @Schema(description = "锁单人是否当前登录人 0否 1是")
    private String isLocker;

    @Schema(description = "之前审核人是否当前登录人 0否 1是")
    private String isReviewer;

    @Schema(description = "CPF")
    private String cpf;

}
