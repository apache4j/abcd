package com.cloud.baowang.agent.api.vo.agentreview.info;


import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "代理信息修改审核分页查询入参")
public class AgentInfoModifyReviewPageQueryVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "操作人", hidden = true)
    private String operator;
    @Schema(description = "申请时间 开始")
    private Long applyStartTime;
    @Schema(description = "申请时间 结束")
    private Long applyEndTime;
    @Schema(description = "一审完成时间 开始")
    private Long firstFinishStartTime;
    @Schema(description = "一审完成时间 结束")
    private Long firstFinishEndTime;
    @Schema(description = "代理账号")
    private String agentAccount;
    @Schema(description = "账号类型")
    private Integer agentType;
    @Schema(description = "审核操作,system_param review_operation code值")
    private Integer reviewOperation;
    @Schema(description = "审核状态 system_param review_status code值")
    private Integer reviewStatus;
    @Schema(description = "申请人")
    private String applicant;
    @Schema(description = "一审人")
    private String firstInstance;
    @Schema(description = "锁单状态 system_param lock_status code值")
    private Integer lockStatus;
    @Schema(description = "审核单号")
    private String agentReviewOrderNo;
    /**
     * 数据脱敏 true 需要脱敏 false 不需要脱敏
     */
    private Boolean dataDesensitization;
}
