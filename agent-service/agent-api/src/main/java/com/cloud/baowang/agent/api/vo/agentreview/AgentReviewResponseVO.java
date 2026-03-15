package com.cloud.baowang.agent.api.vo.agentreview;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author: kimi
 */
@Data
@Schema(description = "代理审核列表 返回")
@I18nClass
public class AgentReviewResponseVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(description = "审核单号")
    private String reviewOrderNo;

    @Schema(description = "代理编号")
    private String agentId;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理层级")
    private Integer level;

    @Schema(description = "代理层级名称")
    private String levelName;

    @Schema(description = "申请信息")
    private String applyInfo;

    @Schema(description = "申请时间")
    private Long applyTime;

    @Schema(description = "申请人")
    private String applicant;

    @Schema(description = "一审完成时间")
    private Long oneReviewFinishTime;

    @Schema(description = "一审人")
    private String reviewer;

    @Schema(description = "审核操作 1一审审核 2结单查看")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_OPERATION)
    private Integer reviewOperation;

    @Schema(description = "审核操作 1一审审核 2结单查看")
    private String reviewOperationText;

    @Schema(description = "审核状态 1待处理 2处理中 3审核通过 4一审拒绝")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer reviewStatus;

    @Schema(description = "审核状态 1待处理 2处理中 3审核通过 4一审拒绝")
    private String reviewStatusText;

    @Schema(description = "锁单状态 0未锁 1已锁")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_LOCK_STATUS)
    private Integer lockStatus;

    @Schema(description = "锁单状态 0未锁 1已锁")
    private String lockStatusText;

    @Schema(description = "锁单人")
    private String locker;

    @Schema(description = "锁单人是否当前登录人 0否 1是")
    private Integer isLocker;

    @Schema(description = "申请人是否当前登录人 0否 1是")
    private Integer isApplicant;
}
