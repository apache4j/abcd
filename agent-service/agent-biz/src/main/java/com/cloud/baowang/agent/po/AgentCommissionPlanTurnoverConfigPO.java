package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 有效流水佣金方案配置项实体类
 *
 * @author remo
 */
@Data
@TableName("agent_commission_plan_turnover_config")
@Schema(title = "有效流水佣金方案配置项实体类", description = "有效流水佣金方案配置项实体类")
public class AgentCommissionPlanTurnoverConfigPO extends BasePO {
    @Schema(title = "方案编码")
    private String planCode;

    @Schema(title = "游戏类型")
    private Integer venueType;

    @Schema(title = "币种")
    private String currency;

    @Schema(title = "等级")
    private Integer tierNum;

    @Schema(title = "有效投注")
    private BigDecimal betAmount;

    @Schema(title = "返佣比例")
    private BigDecimal rate;
    
}
