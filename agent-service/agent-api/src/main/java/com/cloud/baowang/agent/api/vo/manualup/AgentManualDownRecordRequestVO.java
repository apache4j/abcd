package com.cloud.baowang.agent.api.vo.manualup;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title = "代理人工扣除记录 请求对象")
public class AgentManualDownRecordRequestVO extends PageVO {

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(title = "操作开始时间")
    private Long applyStartTime;

    @Schema(title = "操作结束时间")
    private Long applyEndTime;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "代理姓名")
    private String agentName;

    @Schema(title = "订单状态")
    private String orderStatus;

    @Schema(title = "账变状态，0.账变失败，1.账变成功 system_param balance_change_status code")
    private Integer balanceChangeStatus;

    @Schema(title = "调整类型")
    private Integer adjustType;

    @Schema(title = "调整金额最小值")
    private BigDecimal minAdjustAmount;

    @Schema(title = "调整金额最大值")
    private BigDecimal maxAdjustAmount;

    @Schema(title = "是否导出 true是 false否")
    private Boolean exportFlag = false;
}
