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
@Schema(title = "代理提款记录 分页查询 接参对象")
public class AgentWithdrawalRecordReqVO extends PageVO {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "type")
    private Integer type;

    @Schema(description = "审核操作状态")
    private Integer reviewOperation;
    @Schema(description = "申请时间-开始")
    private Long applyTimeStart;
    @Schema(description = "申请时间-结束")
    private Long applyTimeEnd;

    @Schema(description = "审核时间-开始时间")
    private Long auditTimeStart;

    @Schema(description = "审核时间-结束时间")
    private Long auditTimeEnd;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "订单来源")
    private String deviceType;

    @Schema(title = "订单状态")
    private Integer status;

    @Schema(title = "客户端状态")
    private Integer customerStatus;

    @Schema(title = "币种")
    private String currencyCode;

    @Schema(title = "提款方式")
    private String depositWithdrawWayId;

    @Schema(title = "提款通道code")
    private String depositWithdrawChannelName;

    @Schema(title = "是否为大额提款")
    private Integer isBigMoney;

    @Schema(title = "是否为首提")
    private Integer isFirstOut;



}
