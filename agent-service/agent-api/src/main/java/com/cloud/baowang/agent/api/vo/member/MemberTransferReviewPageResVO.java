package com.cloud.baowang.agent.api.vo.member;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "会员转代审核列表响应")
@I18nClass
public class MemberTransferReviewPageResVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "审核单号")
    private String eventId;

    @Schema(description = "转代会员ID")
    private String userAccount;

    @Schema(description = "当前上级代理账号")
    private String currentAgentName;

    @Schema(description = "当前上级代理id")
    private String currentAgentId;

    @Schema(description = "转入上级代理账号")
    private String transferAgentName;

    @Schema(description = "转入上级代理id")
    private String transferAgentId;

    @Schema(description = "申请时间")
    private Long createdTime;

    @Schema(description = "申请备注")
    private String applyRemark;

    @Schema(description = "锁单人id")
    private String lockerId;

    @Schema(description = "锁单状态 0未锁 1已锁 system_param lock_status code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_LOCK_STATUS)
    private Integer lockStatus;

    @Schema(description = "锁单状态名称")
    private String lockStatusText;

    @Schema(description = "锁单时间")
    private Long lockDatetime;

    @Schema(description = "锁单人--审核分页列表,使用此字段作为审核人")
    private String lockName;

    @Schema(description = "审核状态（0-待处理 1-处理中，2-审核通过，3-审核拒绝）")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer auditStatus;

    @Schema(description = "审核状态名称")
    private String auditStatusText;

    @Schema(description = "审核完成时间")
    private Long auditDatetime;

    @Schema(description = "审核人id")
    private String auditId;

    @Schema(description = "审核人")
    private String auditName;

    @Schema(description = "审核备注")
    private String auditRemark;

    @Schema(description = "申请人")
    private String applyName;

    /**
     * {@link com.cloud.baowang.common.core.enums.ReviewOperationEnum}
     * 审核环节 1.一审审核，2.结单查看 同system_param review_operation code
     */
    @Schema(description = "审核环节 1.一审审核，2.结单查看 同system_param review_operation code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_OPERATION)
    private Integer auditStep;
    @Schema(description = "审核环节名称")
    private String auditStepText;

    @Schema(description = "锁单人是否当前登录人 0否 1是")
    private Integer isLocker;

    @Schema(description = "申请人是否当前登录人 0否 1是")
    private Integer isApplicant;

    @Schema(description = "审核用时")
    private String reviewDuration;

    private String siteCode;
}
