package com.cloud.baowang.agent.api.vo.depositWithdraw;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description ="代理代存列表请求对象")
public class AgentDepositSiteRecordPageVO extends PageVO {

    @Schema(description ="开始时间")
    @NotNull(message = "开始时间不能为空")
    private Long startTime;

    @Schema(description ="结束时间")
    @NotNull(message = "结束时间不能为空")
    private Long endTime;

    @Schema(description ="订单号")
    private String orderNo;

    @Schema(description ="代理账号")
    private String agentAccount;

//    @Schema(description ="代理会员账号")
//    private String userAccount;

    @Schema(description = "代存类型（0-处理中 1-成功 2-失败）")
    private String depositSubordinatesType;

    @Schema(description ="代存金额-最小数值")
    private BigDecimal amountMin;

    @Schema(description ="代存金额-最大数值")
    private BigDecimal amountMax;

//    @Schema(description = "订单状态 0-成功,1-失败,2-处理中")
//    private String status;

    @Schema(description = "是否导出查询 true 是 false 否", hidden = true)
    private Boolean exportFlag = false;
    @Schema(description = "站点编号", hidden = true)
    private String siteCode;
}
