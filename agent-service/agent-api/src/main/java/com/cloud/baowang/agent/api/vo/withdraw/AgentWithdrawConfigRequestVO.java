package com.cloud.baowang.agent.api.vo.withdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "提款配置信息请求")
public class AgentWithdrawConfigRequestVO {


    /**
     * 提款方式ID
     */
    @Schema(description = "提款方式ID")
    private String withdrawWayId;



    private String agentId;

}
