package com.cloud.baowang.agent.api.vo.merchant;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 商务审核信息
 */
@Data
@Schema(description = "商务审核信息vo")
@I18nClass
public class AgentMerchantReviewRecordVO implements Serializable {

    /**
     * 主键
     */
    @Schema(description = "主键")
    private String id;

    /**
     * 商务账号
     */
    @Schema(description = "商务账号")
    private String merchantAccount;

    /**
     * 站点编号
     */
    @Schema(description = "站点编号")
    private String siteCode;

    /**
     * 订单编号
     */
    @Schema(description = "订单编号")
    private String orderNo;

    /**
     * 商务名称
     */
    @Schema(description = "商务名称")
    private String merchantName;

    /**
     * 申请人
     */
    @Schema(description = "申请人")
    private String applicant;

    /**
     * 申请时间
     */
    @Schema(description = "申请时间")
    private Long applicationTime;

    /**
     * 申请备注
     */
    @Schema(description = "申请备注")
    private String applicationRemark;

    /**
     * 审核状态,通system_param review_status
     * {@link com.cloud.baowang.common.core.enums.ReviewStatusEnum}
     */
    @Schema(description = "审核状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer reviewStatus;

    private String reviewStatusText;

    /**
     * 审核操作(归集状态,同system_param review_operation)
     * {@link com.cloud.baowang.common.core.enums.ReviewOperationEnum}
     */
    @Schema(description = "审核操作")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_OPERATION)
    private Integer reviewOperation;

    private String reviewOperationText;

    @Schema(description = "锁单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_LOCK_STATUS)
    private Integer lockStatus;

    private String lockStatusText;

    /**
     * 锁单人
     */
    @Schema(description = "锁单人")
    private String locker;

    /**
     * 锁单时间
     */
    @Schema(description = "锁单时间")
    private Long lockTime;

    /**
     * 审核人
     */
    @Schema(description = "审核人")
    private String auditName;

    /**
     * 审核时间
     */
    @Schema(description = "审核时间")
    private Long auditTime;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "申请人是否是当前登录人")
    private Integer isApplicant;

    @Schema(description = "锁单人是否是当前登录人")
    private Integer isLock;


}
