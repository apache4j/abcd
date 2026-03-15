package com.cloud.baowang.agent.api.vo.member;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "会员溢出审核列表请求对象")
public class MemberOverflowReviewPageReqVO extends PageVO {

    @Schema(description = "站点编码", hidden = true)
    private String siteCode;

    @Schema(description = "申请时间-开始")
    private Long applyTimeStart;

    @Schema(description = "申请时间-结束")
    private Long applyTimeEnd;

    @Schema(description = "审核时间-开始")
    private Long auditTimeStart;

    @Schema(description = "审核时间-结束")
    private Long auditTimeEnd;

    @Schema(description = "申请代理账号")
    private String transferAgentName;

    /**
     * UserAccountTypeEnum
     */
    @Schema(description = "账号类型")
    private Integer accountType;

    @Schema(description = "审核单号")
    private String eventId;
    /**
     * {@link com.cloud.baowang.common.core.enums.ReviewOperationEnum}
     */
    @Schema(description = "审核操作 1-结单查看 2-一审审核 同system_param review_operation code值",hidden = true)
    private Integer auditStep;
    /**
     * {@link com.cloud.baowang.common.core.enums.ReviewStatusEnum}
     */
    @Schema(description = "订单状态（1-待处理 2-处理中，3-审核通过，4-审核拒绝）同system_param review_status code值")
    private Integer auditStatus;

    @Schema(description = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;

    @Schema(description = "申请人")
    private String applyName;

    @Schema(description = "审核员账号")
    private String auditName;

    @Schema(description = "溢出会员账号")
    private String memberName;

    @Schema(description = "转代会员账号列表")
    private List<String> userAccounts;
}
