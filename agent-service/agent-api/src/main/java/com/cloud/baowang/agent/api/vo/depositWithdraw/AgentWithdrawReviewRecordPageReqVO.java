package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(title = "代理提款审核记录列表请求对象")
public class AgentWithdrawReviewRecordPageReqVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "存提款类型",hidden = true)
    private Integer type;
    @Schema(description = "申请时间-开始")
    private Long applyTimeStart;

    @Schema(description = "申请时间-结束")
    private Long applyTimeEnd;

    @Schema(description = "审核时间-开始")
    private Long auditTimeStart;

    @Schema(description = "审核时间-结束")
    private Long auditTimeEnd;


    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "代理账号")
    private String agentAccount;
    @Schema(description = "订单状态")
    private Integer status;
    @Schema(description = "币种")
    private String currencyCode;
    @Schema(description = "审核人账号")
    private String auditId;

    @Schema(description = "收款账户")
    private String depositWithdrawAddress;


}
