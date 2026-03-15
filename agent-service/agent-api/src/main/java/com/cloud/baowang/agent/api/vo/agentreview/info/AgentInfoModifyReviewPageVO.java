package com.cloud.baowang.agent.api.vo.agentreview.info;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "代理信息变更审核分页查询返回实体")
@I18nClass
public class AgentInfoModifyReviewPageVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = " 申请时间 提交审核的时间信息 ")
    private Long applicationTime;

    @Schema(description = " 一审完成时间 一审完成后的的时间信息 ")
    private Long firstReviewTime;

    @Schema(description = " 审核单号 系统生成 ")
    private String reviewOrderNumber;

    @Schema(description = " 审核操作")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_OPERATION)
    private Integer reviewOperation;

    @Schema(description = " 审核操作 文本")
    private String reviewOperationText;

    @Schema(description = " 审核状态 ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private String reviewStatus;

    @Schema(description = " 审核状态 文本")
    private String reviewStatusText;

    @Schema(description = " 申请人 审核提出的后台账号信息 ")
    private String applicant;

    @Schema(description = " 一审人 一审审核的后台账号信息 ")
    private String firstInstance;

    @Schema(description = " 锁单状态 1锁单 0 未锁")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_LOCK_STATUS)
    private Integer lockStatus;

    @Schema(description = " 锁单状态 1锁单 0 未锁")
    private String lockStatusText;

    @Schema(description = " 审核申请类型 ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_CHANGE_TYPE)
    private Integer reviewApplicationType;

    @Schema(description = " 审核申请类型 文本")
    private String reviewApplicationTypeText;

    @Schema(description = " 代理账号 注册成功后的登录账号信息 ")
    private String agentAccount;

    @Schema(description = " 账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_TYPE)
    private Integer agentType;

    @Schema(description = " 账号类型文本 ")
    private String agentTypeText;

    @Schema(description = " 修改前 对应审核类型审核前最近的原始数据信息 ")
    private String beforeFixing;

    @Schema(description = " 修改后 对应审核类型审核时需要修改的数据信息 ")
    private String afterModification;

    @Schema(description = "锁单人")
    private String locker;

    @Schema(description = "登录用户是否申请人 0否 1是")
    private Integer isApplicant;

    @Schema(description = "锁单人是否登录用户 0否 1是")
    private Integer isLoginLocker;

    @Schema(description = "一审完成备注")
    private String reviewRemark;

    @Schema(description = " 申请信息")
    private String applicationInformation;
}
