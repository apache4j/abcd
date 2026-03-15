package com.cloud.baowang.agent.api.vo.merchant;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "商务信息修改审核记录vo")
@I18nClass
public class AgentMerchantModifyReviewVO {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "注册时间")
    private Long registerTime;

    /**
     * 商务账号
     */
    @Schema(description = "商务账号")
    private String merchantAccount;

    @Schema(description = "商务名称")
    private String merchantName;

    @Schema(description = "上次登录时间")
    private Long lastLoginTime;

    /**
     * 申请时间
     */
    @Schema(description = "申请时间")
    private Long applicationTime;

    /**
     * 一审完成时间
     */
    @Schema(description = "审核时间")
    private Long firstReviewTime;

    /**
     * 审核单号
     */
    @Schema(description = "单号")
    private String reviewOrderNumber;

    /**
     * 审核操作 system_param review_operation值
     */
    @Schema(description = "审核操作")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_OPERATION)
    private Integer reviewOperation;

    private String reviewOperationText;

    /**
     * 审核状态 system_param review_status值
     */
    @Schema(description = "审核状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer reviewStatus;

    private String reviewStatusText;

    /**
     * 申请人
     */
    @Schema(description = "申请人")
    private String applicant;

    /**
     * 一审人
     */
    @Schema(description = "审核人")
    private String firstInstance;

    /**
     * 锁单状态 1锁单 0解锁
     */
    @Schema(description = "锁单状态 1锁单 0解锁")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_LOCK_STATUS)
    private Integer lockStatus;

    private String lockStatusText;

    /**
     * 审核申请类型
     */
    @Schema(description = "审核申请类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.MERCHANT_MODIFY_TYPE)
    private Integer reviewApplicationType;

    private String reviewApplicationTypeText;

    /**
     * 修改前
     */
    @Schema(description = "修改前")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_STATUS)
    private String beforeFixing;

    private String beforeFixingText;

    /**
     * 修改后
     */
    @Schema(description = "修改后")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_STATUS)
    private String afterModification;

    private String afterModificationText;

    /**
     * 锁单人
     */
    @Schema(description = "锁单人")
    private String locker;

    /**
     * 一审完成备注
     */
    @Schema(description = "审核备注")
    private String reviewRemark;

    /**
     * 申请信息
     */
    @Schema(description = "申请信息")
    private String applicationInformation;

    @Schema(description = "申请人是否是当前登录人")
    private Integer isApplicant;

    @Schema(description = "锁单人是否是当前登录人")
    private Integer accountIsLocker;

}
