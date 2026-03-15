package com.cloud.baowang.wallet.api.vo.fundrecord;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(title = "会员人工加额审核-列表 返回")
@I18nClass
public class UserManualUpReviewResponseVO {

    @Schema(title = "id")
    private String id;

    @Schema(description = "操作 1.一审审核，2.结单查看 system_param review_operation code值")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_OPERATION)
    private Integer reviewOperation;
    @Schema(description = "操作")
    private String reviewOperationText;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "会员ID")
    private String userId;

    @Schema(description = "会员注册信息")
    private String userAccount;

    @Schema(description = "会员对应代理id")
    private String agentId;

    @Schema(description = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.MANUAL_ADJUST_TYPE)
    private Integer adjustType;

    @Schema(description = "调整类型 - Text")
    private String adjustTypeText;

    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;

    @Schema(description = "申请时间")
    private Long applyTime;
    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "审核状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer auditStatus;

    @Schema(description = "审核状态 - Text")
    private String auditStatusText;

    @Schema(description = "锁单状态 0未锁 1已锁")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_LOCK_STATUS)
    private Integer lockStatus;
    @Schema(description = "锁单状态")
    private String lockStatusText;
    @Schema(description = "审核员/锁单人")
    private String locker;

    @Schema(description = "锁单人是否当前登录人 0否 1是")
    private Integer isLocker;

    @Schema(description = "申请人")
    private String applicant;
    @Schema(description = "申请人是否当前登录人 0否 1是")
    private Integer isApplicant;

    @Schema(description = "审核人")
    private String auditId;
    @Schema(description = "一审人是否当前登录人 0否 1是")
    private Integer isAuditId;
}
