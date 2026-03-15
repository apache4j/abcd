package com.cloud.baowang.agent.po.commission;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
@Data
@TableName("agent_commission_venue_report")
public class AgentCommissionVenueReportPO {
    @TableId
    private Long id;

    /** 站点编码 */
    @TableField("site_code")
    private String siteCode;

    /** 代理ID */
    @TableField("agent_id")
    private String agentId;

    @TableField("venue_type")
    private Integer venueType;

    /** 佣金类型(0-直属会员 1-下级代理) */
    @TableField("commission_type")
    private Integer commissionType;

    /** 佣金比例 */
    @TableField("plan_rate")
    private BigDecimal planRate;

    /** 佣金极差比例 */
    @TableField("diff_rate")
    private BigDecimal diffRate;

    /** 佣金金额 */
    @TableField("commission_amount")
    private BigDecimal commissionAmount;

    /** 有效流水 */
    @TableField("valid_amount")
    private BigDecimal validAmount;

    /** 统计开始时间 */
    @TableField("start_time")
    private Long startTime;

    /** 统计结束时间 */
    @TableField("end_time")
    private Long endTime;

    //创建时间
    private Long applyTime;
}
