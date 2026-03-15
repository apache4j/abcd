package com.cloud.baowang.agent.api.vo.member;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "会员溢出审核列表响应对象")
@I18nClass
public class MemberOverflowReviewPageResVO implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "审核单号")
    private String eventId;

    @Schema(description = "申请人")
    private String applyName;

    @Schema(description = "申请代理账号")
    private String transferAgentName;
    @Schema(description = "申请代理id")
    private String transferAgentId;

    @Schema(description = "代理类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_TYPE)
    private Integer agentType;

    @Schema(description = "代理类型")
    private String agentTypeText;

    @Schema(description = "溢出会员账号")
    private String memberName;

    @Schema(description = "账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private Integer accountType;

    @Schema(description = "账号类型")
    private String accountTypeText;

    @Schema(description = "申请时间")
    private Long createdTime;

    @Schema(description = "申请备注")
    private String applyRemark;

    /**
     * {@link com.cloud.baowang.common.core.enums.ReviewStatusEnum}
     */
    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer auditStatus;

    @Schema(description = "订单状态")
    private String auditStatusText;

    @Schema(description = "审核时间")
    private Long auditDatetime;

    @Schema(description = "审核用时")
    private String reviewDuration;

    @Schema(description = "审核人")
    private String auditName;

    @Schema(description = "锁单人(审核列表审核人展示这个)")
    private String lockName;

    @Schema(description = "锁单状态（0-未锁定 1-已锁定）")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_LOCK_STATUS)
    private Integer lockStatus;

    @Schema(description = "锁单状态名称")
    private String lockStatusText;

    @Schema(description = "锁单时间(审核列表审核时间展示用这个)")
    private Long lockDatetime;


    @Schema(description = "备注")
    private String auditRemark;

    /**
     * 审核环节（1.结单查看 2.一审审核）
     * {@link com.cloud.baowang.common.core.enums.ReviewOperationEnum}
     */
    @Schema(description = "审核环节（1.结单查看 2.一审审核）")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_OPERATION)
    private Integer auditStep;

    @Schema(description = "审核环节名称")
    private String auditStepText;

    /**
     * 是否是锁单人
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     */
    @Schema(description = "锁单人是否当前登录人 0否 1是")
    private Integer isLocker;
    /**
     * 是否是申请人
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     */
    @Schema(description = "申请人是否当前登录人 0否 1是")
    private Integer isApplicant;

    private String siteCode;

}
