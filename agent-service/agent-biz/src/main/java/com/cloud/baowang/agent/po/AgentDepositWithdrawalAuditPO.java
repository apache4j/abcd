package com.cloud.baowang.agent.po;


import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.FieldNameConstants;

/**
 * 代理存款取款审核信息
 *
 * @author qiqi
 */
@Data
@Accessors(chain = true)
@FieldNameConstants
@TableName("agent_deposit_withdrawal_audit")
public class AgentDepositWithdrawalAuditPO extends BasePO {

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 第x审核
     */
    private Integer num;

    /**
     * 审核人员
     */
    private String auditUser;

    /**
     * 锁单时间
     */
    private Long lockTime;

    /**
     * 审核时间
     */
    private Long auditTime;

    /**
     * 审核耗时 单位秒
     */
    private Long auditTimeConsuming;

    /**
     * 审核状态 1通过 2拒绝
     */
    private Integer auditStatus;

    /**
     * 审核信息
     */
    private String auditInfo;
}
