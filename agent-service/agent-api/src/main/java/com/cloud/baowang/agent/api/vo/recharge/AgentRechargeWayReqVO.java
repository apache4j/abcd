package com.cloud.baowang.agent.api.vo.recharge;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@Schema(title = "代理存款请求方式VO")
public class AgentRechargeWayReqVO {

    /**
     * 充值方式id
     */
    @Schema(description = "方式id")
    private String depositWayId;


}
