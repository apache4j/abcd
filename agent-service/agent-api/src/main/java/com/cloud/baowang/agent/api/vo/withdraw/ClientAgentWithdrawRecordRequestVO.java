package com.cloud.baowang.agent.api.vo.withdraw;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@Schema(title = "代理端取款记录请求对象")
public class ClientAgentWithdrawRecordRequestVO extends PageVO {

    @Schema(description = "开始时间")
    private Long startTime;

    @Schema(description = "结束时间")
    private Long endTime;

    private String agentId;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "客户端状态")
    private String customerStatus;


}
