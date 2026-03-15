package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代理存款记录 请求对象")
public class AgentDepositRecordReq extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

    @Schema(description = "申请开始时间")
    private Long applyStartTime;

    @Schema(description = "申请结束时间")
    private Long applyEndTime;

    @Schema(description = "完成开始时间")
    private Long finishStartTime;

    @Schema(description = "完成结束时间")
    private Long finishEndTime;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "订单来源")
    private Integer deviceType;

    @Schema(description = "订单状态")
    private Integer status;

    @Schema(description = "客户端状态")
    private String customerStatus;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "存款方式")
    private String depositWithdrawWay;

    @Schema(description = "存款通道code")
    private String depositWithdrawChannelCode;
    @Schema(description = "排序")
    private String orderType;
}
