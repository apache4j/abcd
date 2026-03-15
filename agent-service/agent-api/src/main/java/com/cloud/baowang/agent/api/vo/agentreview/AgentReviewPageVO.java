package com.cloud.baowang.agent.api.vo.agentreview;

import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 代理审核列表 Request
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Schema(description = "代理审核列表 Request")
public class AgentReviewPageVO extends SitePageVO {

    @Schema(description = "申请时间-开始")
    private Long applyTimeStart;

    @Schema(description = "申请时间-结束")
    private Long applyTimeEnd;

    @Schema(description = "一审完成时间-开始")
    private Long oneReviewFinishTimeStart;

    @Schema(description = "一审完成时间-结束")
    private Long oneReviewFinishTimeEnd;

    @Schema(description = "审核状态 1待处理 2处理中 3审核通过 4一审拒绝")
    private List<Integer> reviewStatus;

    @Schema(description = "锁单状态 0未锁 1已锁")
    private Integer lockStatus;

    @Schema(description = "审核操作 1一审审核 2结单查看")
    private Integer reviewOperation;

    @Schema(description = "申请人")
    private String applicant;

    @Schema(description = "一审人")
    private String reviewer;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "审核订单号")
    private String reviewOrderNo;
}
