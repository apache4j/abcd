package com.cloud.baowang.agent.api.vo.agentreview.info;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "代理信息变更审核详情返回实体")
@I18nClass
public class AgentInfoModifyReviewDetailVO {
    //注册信息
    @Schema(description = "注册端")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEVICE_TYPE)
    private Integer registerDeviceType;

    @Schema(description = "注册端 文本")
    private String registerDeviceTypeText;

    @Schema(description = "注册时间")
    private Long registerTime;

    @Schema(description = "上次登录时间")
    private Long lastLoginTime;
    //账号信息
    @Schema(description = "代理标签id")
    private String agentLabelId;
    @Schema(description = "代理标签文本")
    private String agentLabelText;

    @Schema(description = "风控层级id")
    private String riskLevelId;
    @Schema(description = "风控层级文本")
    private String riskLevelText;

    @Schema(description = "账号状态 1正常 2登录锁定 3充提锁定(状态多选,用逗号分开)")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_STATUS)
    private String status;

    @Schema(description = "账号状态文本")
    private String statusText;

    @Schema(description = "代理层级")
    private Integer level;

    @Schema(description = "备注信息")
    private String remark;


    @Schema(description = " 申请时间 提交审核的时间信息 ")
    private Long applicationTime;

    @Schema(description = " 一审完成时间 一审完成后的的时间信息 ")
    private Long firstReviewTime;

    @Schema(description = " 审核单号 系统生成 ")
    private String reviewOrderNumber;

    @Schema(description = " 审核操作 ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_OPERATION)
    private Integer reviewOperation;

    @Schema(description = " 审核操作 文本")
    private String reviewOperationText;

    @Schema(description = " 审核状态 ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer reviewStatus;

    @Schema(description = " 审核状态 文本 ")
    private String reviewStatusText;

    @Schema(description = " 申请人 审核提出的后台账号信息 ")
    private String applicant;

    @Schema(description = " 一审人 一审审核的后台账号信息 ")
    private String firstInstance;

    @Schema(description = " 锁单状态 1锁单 0 未锁")
    private String lockStatus;

    @Schema(description = " 审核申请类型 ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_CHANGE_TYPE)
    private Integer reviewApplicationType;

    @Schema(description = " 审核申请类型 文本")
    private String reviewApplicationTypeText;

    @Schema(description = " 代理账号 注册成功后的登录账号信息 ")
    private String agentAccount;

    @Schema(description = "上级代理账号")
    private String parentAccount;
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

    @Schema(description = "锁单人是否当前登录人 0否 1是")
    private Integer isLocker;

    @Schema(description = "一审完成备注")
    private String reviewRemark;

    @Schema(description = " 申请信息")
    private String applicationInformation;

    @Schema(description = "父节点")
    private String parentId;



    /**
     * 所属商务账号
     */
    @Schema(description = "商务账号")
    private String merchantAccount;

    /**
     * 所属商务名称
     */
    @Schema(description = "商务名称")
    private String merchantName;
}
