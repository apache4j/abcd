package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/06/04 14:39
 * @description: 代理佣金方案配置
 */
@Data
@TableName("agent_commission_plan")
@Schema(title = "代理佣金方案配置", description = "代理佣金方案配置")
public class AgentCommissionPlanPO extends BasePO {
    @Schema(title = "站点code")
    private String siteCode;

    @Schema(title = "方案code")
    private String planCode;

    @Schema(title = "方案名称")
    private String planName;

    @Schema(title = "活跃人数最少充值金额")
    private BigDecimal activeDeposit;

    @Schema(title = "活跃人数最少有效投注金额")
    private BigDecimal activeBet;

    @Schema(title = "有效新增最少充值金额")
    private BigDecimal validDeposit;

    @Schema(title = "有效新增最少有效投注额")
    private BigDecimal validBet;

    @Schema(title = "方案状态  0 已编辑  1 未编辑")
    private Integer status;
}
