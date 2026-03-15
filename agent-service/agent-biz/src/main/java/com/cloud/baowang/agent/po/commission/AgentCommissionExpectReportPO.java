package com.cloud.baowang.agent.po.commission;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2023/10/25 1:10
 * @description: 代理佣金预期表
 */
@Data
@TableName("agent_commission_expect_report")
@Schema(title = "AgentCommissionExpectReportPO对象", description = "代理佣金预期表")
public class AgentCommissionExpectReportPO extends BasePO {
    /** siteCode */
    private String siteCode;
    /** 代理账号 */
    private String agentAccount;

    /** 代理ID */
    private String agentId;

    /** 上级代理Id */
    private String superAgentId;

    /** 代理类型 */
    private Integer agentType;

    /** 代理层级 */
    private Integer agentLevel;

    /** 风控层级id */
    private String riskLevelId;

    /** 结算开始时间 */
    private Long startTime;

    /** 结算结束时间 */
    private Long endTime;

    /** 结算周期  1 自然日 2 自然周  3 自然月 */
    private Integer settleCycle;

    /** 状态 */
    private Integer status;

    /** 提前结算 */
    private BigDecimal earlySettle;

    /** 会员输赢 */
    private BigDecimal userWinLoss;

    /** 会员总输赢 */
    private BigDecimal userWinLossTotal;

    /** 场馆费 */
    private BigDecimal venueFee;

    /** 平台币钱包转化金额 */
    private BigDecimal transferAmount;

    /** 总存取手续费 */
    private BigDecimal accessFee;

    /** 调整金额 */
    private BigDecimal adjustAmount;

    /** 活动优惠 */
    private BigDecimal discountAmount;

    /** vip福利 */
    private BigDecimal vipAmount;

    /** 有效流水 */
    private BigDecimal validBetAmount;

    /** 待冲正金额 */
    private BigDecimal lastMonthRemain;


    /** 会员净输赢 */
    private BigDecimal netWinLoss;

    /** 返佣比例 */
    private BigDecimal agentRate;

    /** 有效活跃人数 */
    private Integer activeNumber;

    /** 有效新增活跃会员数 */
    private Integer newValidNumber;

    /** 负盈利佣金 */
    private BigDecimal commissionAmount;

    /** 打赏金额 */
    private BigDecimal tipsAmount;

    /** 会员输赢 */
    private BigDecimal betWinLoss;

    /** 佣金方案code */
    private String planCode;
}
