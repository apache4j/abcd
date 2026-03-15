package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/10/21 17:44
 * @description: 比例数据
 */
@Data
@I18nClass
public class RateDetailVO {
    @Schema(title = "代理ID")
    private String agentId;
    @Schema(title = "负盈利佣金比例")
    private BigDecimal agentRate;
    @Schema(title = "有效流水返点比例")
    private AgentRebateRateVO agentRebateRateVO;
}
