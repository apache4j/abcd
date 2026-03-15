package com.cloud.baowang.agent.po.commission;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/08 19:42
 * @description:agent_commission_grant_record
 */
@Data
@TableName("agent_commission_grant_record")
@Schema(title = "代理佣金发放记录表", description = "代理佣金发放记录表")
public class AgentCommissionGrantRecordPO extends BasePO {
    /** siteCode */
    private String siteCode;
    /**agent_commission_final_report  或者 agent_rebate_final_report 主键id*/
    private String reportId;
    /** 代理id */
    private String agentId;
    /** 代理账号 */
    private String agentAccount;
    /** 代理类型 1正式 2测试 3合作 */
    private Integer agentType;
    /** 代理类别 1常规代理 2流量代理*/
    private Integer agentCategory;
    /** 注册时间 */
    private Long registerTime;
    /** 结算周期  1 自然日 2 自然周  3 自然月 */
    private Integer settleCycle;
    /** 佣金类型 */
    private String commissionType;
    /** 佣金方案id */
    private String planId;
    /** 币种 */
    private String currency;

    private Long startTime;

    private Long endTime;
    /** 发放时间 */
    private Long grantTime;
    /** 结算金额 */
    private BigDecimal commissionAmount;
    //商务号
    private String merchantAccount;
    //商务名称
    private String merchantName;

    /** 申请金额 */
    private BigDecimal applyAmount;

    /** 调整金额 */
    private BigDecimal adjustAmount;



}
