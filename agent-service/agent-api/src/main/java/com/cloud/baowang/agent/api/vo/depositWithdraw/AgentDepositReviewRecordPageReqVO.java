package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(title = "代理存款审核记录列表请求对象")
public class AgentDepositReviewRecordPageReqVO extends PageVO {

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "operator", hidden = true)
    private String operator;


    @Schema(description = "申请时间-开始")
    private Long applyTimeStart;

    @Schema(description = "申请时间-结束")
    private Long applyTimeEnd;

    @Schema(description = "审核时间-开始时间")
    private Long auditTimeStart;

    @Schema(description = "审核时间-结束时间")
    private Long auditTimeEnd;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "订单状态")
    private String depositWithdrawStatus;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "审核员账号")
    private String auditUser;


    @Schema(description = "币种")
    private String currencyCode;


    @Schema(description = "存款方式")
    private String depositWithdrawWayId;


}
