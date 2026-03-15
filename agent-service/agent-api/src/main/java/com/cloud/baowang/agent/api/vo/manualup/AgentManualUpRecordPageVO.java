package com.cloud.baowang.agent.api.vo.manualup;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 代理人工加额记录 Request
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Schema(title = "代理人工加额记录 Request")
public class AgentManualUpRecordPageVO extends PageVO {

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "审核操作", hidden = true)
    private Integer reviewOperation;

    @Schema(title = "申请时间-开始")
    private Long applyStartTime;

    @Schema(title = "申请时间-结束")
    private Long applyEndTime;

    @Schema(description = "审核时间-开始")
    private Long oneReviewFinishTimeStart;

    @Schema(description = "审核时间-结束")
    private Long oneReviewFinishTimeEnd;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "订单状态")
    private Integer orderStatus;

    @Schema(title = "调整类型")
    private Integer adjustType;

    @Schema(title = "调整金额-最小值")
    private String adjustAmountMin;

    @Schema(title = "调整金额-最大值")
    private String adjustAmountMax;
}
