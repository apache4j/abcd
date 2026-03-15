package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/09/19 17:05
 * @description: 流水返点配置
 */
@Data
@TableName("agent_rebate_config")
@Schema(title = "流水返点配置", description = "流水返点配置")
public class AgentRebateConfigPO extends BasePO {
    @Schema(title = "佣金方案ID")
    private String planId;
    @Schema(title = "结算周期  1 自然日 2 自然周  3 自然月")
    private Integer settleCycle;
    @Schema(title = "有效新增人头费")
    private BigDecimal newUserAmount;
    @Schema(title = "电子有效流水返点比例")
    private String slotRate;
    @Schema(title = "彩票有效流水返点比例")
    private String lotteryRate;
    @Schema(title = "彩票赔率方案id")
    private Long lotteryPlanId;
    @Schema(title = "真人有效流水返点比例")
    private String liveRate;
    @Schema(title = " 体育有效流水返点比例")
    private String sportsRate;
    @Schema(title = "体育赔率方案id")
    private Long sportsPlanId;
    @Schema(title = "棋牌有效流水返点比例")
    private String chessRate;
    @Schema(title = "电竞有效流水返点比例")
    private String esportsRate;
    @Schema(title = "电竞赔率方案id")
    private String esportsPlanId;
    @Schema(title = "斗鸡有效流水返点比例")
    private String cockfightRate;
    @Schema(title = "捕鱼返点比例")
    private String fishRate;
    @Schema(description = "娱乐返点比例")
    private String marblesRebate;
}
