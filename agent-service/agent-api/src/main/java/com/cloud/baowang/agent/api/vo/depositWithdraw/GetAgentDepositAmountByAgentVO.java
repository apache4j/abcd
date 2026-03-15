package com.cloud.baowang.agent.api.vo.depositWithdraw;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Map;

/**
 * @author: kimi
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(title = "查询某个代理 在某时间内的代存金额(按照会员分组) VO")
public class GetAgentDepositAmountByAgentVO {

   @Schema(title = "会员账号-代存金额")
    private Map<String, BigDecimal> agentDepositAmountMap;

   @Schema(title = "会员账号-分配次数")
    private Map<String, Long> transAgentTime;
}
