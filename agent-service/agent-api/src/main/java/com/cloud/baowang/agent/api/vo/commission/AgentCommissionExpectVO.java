package com.cloud.baowang.agent.api.vo.commission;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2023/10/25 1:10
 */
@Data
@Schema(description = "代理预期佣金返回VO")
public class AgentCommissionExpectVO implements Serializable {

    @Schema(description = "siteCode")
    private String siteCode;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理ID")
    private String agentId;

    @Schema(description = "上级代理Id")
    private String superAgentId;

    @Schema(description = "代理类型")
    private Integer agentType;

    @Schema(description = "代理层级")
    private Integer agentLevel;

    @Schema(description = "风控层级id")
    private String riskLevelId;

    @Schema(description = "结算开始时间")
    private Long startTime;

    @Schema(description = "结算结束时间")
    private Long endTime;

    @Schema(description = "结算周期  1 自然日 2 自然周  3 自然月 ")
    private Integer settleCycle;

    @Schema(description = "状态")
    private Integer status;

    @Schema(description = "提前结算")
    private BigDecimal earlySettle;

    @Schema(description = "会员输赢")
    private BigDecimal userWinLoss;

    @Schema(description = "会员总输赢")
    private BigDecimal userWinLossTotal;

    @Schema(description = "场馆费")
    private BigDecimal venueFee;

    @Schema(description = "平台币钱包转化金额")
    private BigDecimal transferAmount;

    @Schema(description = "总存取手续费")
    private BigDecimal accessFee;

    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;

    @Schema(description = "活动优惠")
    private BigDecimal discountAmount;

    @Schema(description = "vip福利")
    private BigDecimal vipAmount;

    @Schema(description = "有效流水")
    private BigDecimal validBetAmount;

    @Schema(description = "待冲正金额")
    private BigDecimal lastMonthRemain;

    @Schema(description = "会员净输赢")
    private BigDecimal netWinLoss;

    @Schema(description = "返佣比例")
    private BigDecimal agentRate;

    @Schema(description = "有效活跃人数")
    private Integer activeNumber;

    @Schema(description = "有效新增活跃会员数")
    private Integer newValidNumber;

    @Schema(description = "负盈利佣金")
    private BigDecimal commissionAmount;

    @Schema(description = "佣金方案code")
    private String planCode;


}

