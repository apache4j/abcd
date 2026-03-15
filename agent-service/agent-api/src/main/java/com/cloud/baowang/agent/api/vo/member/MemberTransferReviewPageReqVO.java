package com.cloud.baowang.agent.api.vo.member;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "会员转代分页查询对象")
public class MemberTransferReviewPageReqVO extends PageVO {

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "申请时间-开始")
    private Long applyTimeStart;

    @Schema(description = "申请时间-结束")
    private Long applyTimeEnd;

    @Schema(description = "审核时间-开始")
    private Long auditTimeStart;

    @Schema(description = "审核时间-结束")
    private Long auditTimeEnd;

    @Schema(description = "订单号")
    private String eventId;

    /**
     * {@link com.cloud.baowang.common.core.enums.ReviewStatusEnum}
     */
    @Schema(description = "审核状态（1-待处理 2-处理中，3-审核通过，4-审核拒绝）system_param review_status code值")
    private Integer auditStatus;

    /**
     * {@link com.cloud.baowang.common.core.enums.LockStatusEnum}
     */
    @Schema(description = "锁单状态 0未锁 1已锁 system_param lock_status code")
    private Integer lockStatus;
    /**
     * 改为后端传入
     * 同system_param review_operation code
     * {@link com.cloud.baowang.agent.api.enums.AgentReviewOperationEnum}
     */
    @Schema(description = "审核操作 2-结单查看 1-一审审核 同system_param review_operation code值", hidden = true)
    private Integer auditStep;

    @Schema(description = "申请人")
    private String applyName;

    @Schema(description = "审核人")
    private String auditName;

    @Schema(description = "转代会员账号")
    private String userAccount;

    @Schema(description = "当前代理账号")
    private String currentAgentName;

    @Schema(description = "转入代理账号")
    private String transferAgentName;

    @Schema(description = "转代会员账号列表")
    private List<String> userAccounts;
    @Schema(description = "账号类型")
    private Integer accountType;


}
