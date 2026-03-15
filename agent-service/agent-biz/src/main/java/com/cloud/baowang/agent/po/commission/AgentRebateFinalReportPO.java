package com.cloud.baowang.agent.po.commission;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2023/10/25 1:10
 * @description: 代理返点
 */
@Data
@TableName("agent_rebate_final_report")
@Schema(title = "AgentRebateFinalReportPO", description = "代理返点表")
public class AgentRebateFinalReportPO extends BasePO {
    /** siteCode */
    private String siteCode;

    /** 代理ID */
    private String agentId;

    private String agentAccount;

    /** 上级代理Id */
    private String superAgentId;

    //总代
    private String oneAgentId;

    /** 代理类型 */
    private Integer agentType;

    /** 代理层级 */
    private Integer agentLevel;

    /** 风控层级id */
    private String riskLevelId;

    /** 佣金方案code */
    private String planCode;

    /** 结算周期  1 自然日 2 自然周  3 自然月 */
    private Integer settleCycle;

    /** 结算开始时间 */
    private Long startTime;

    /** 结算结束时间 */
    private Long endTime;

    /** 状态 */
    private Integer status;

    /** 有效流水返点结算金额 */
    private BigDecimal rebateAmount;

    /** 有效新增活跃会员数 */
    private Integer newValidNumber;

    /** 人头费 */
    private BigDecimal newUserAmount;

    /** 人头费/人 */
    private BigDecimal everyUserAmount;


    /** 人头费调整金额 */
    private BigDecimal adjustAmount;


    /** 有效流水佣金调整金额 */
    private BigDecimal rebateAdjustAmount;
}
