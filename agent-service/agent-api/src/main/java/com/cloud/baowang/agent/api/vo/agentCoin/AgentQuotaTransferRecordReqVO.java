package com.cloud.baowang.agent.api.vo.agentCoin;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "额度转账-记录 响应体")
public class AgentQuotaTransferRecordReqVO extends PageVO {


    @Schema(title = "开始时间")
    private String startTime;

    @Schema(title = "结束时间")
    private String endTime;

    private String agentInfoId;


}
