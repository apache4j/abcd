package com.cloud.baowang.agent.po.commission;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.agent.api.enums.commission.CommissionTypeEnum;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2023/10/28 11:49
 * @description: 代理佣金审核记录对象
 */
@Data
@TableName("agent_commission_review_record")
@Schema(title = "AgentCommissionReviewRecordPO对象", description = "代理佣金审核记录对象")
public class AgentCommissionReviewRecordPO extends BasePO {
    /**agent_commission_final_report  主键id*/
    private String reportId;

    private String siteCode;
    /** 代理ID */
    private String agentId;

    /** 代理账号 */
    private String agentAccount;

    /** 代理姓名 */
    private String agentName;

    /** 订单号 */
    private String orderNo;

    /** 账号状态 1正常 2登录锁定 3充提锁定(状态多选,用逗号分开) */
    private String agentStatus;

    /** {@link CommissionTypeEnum}*/
    /** 佣金类型 */
    private String commissionType;

    /** 佣金金额 */
    private BigDecimal commissionAmount;

    /** 结算开始时间 */
    private Long startTime;

    /** 结算结束时间 */
    private Long endTime;

    /** 申请时间 */
    private Long applyTime;

    /** 重算 时间 */
    private Long settleTime;

    /** 是否重算  0 不是  1是 */
    private Integer settleStatus;

    private String currency;

    /**结算周期  1 自然日 2 自然周  3 自然月*/
    private Integer settleCycle;

    /** 一审开始时间 */
    private Long oneReviewStartTime;

    /** 一审完成时间 */
    private Long oneReviewFinishTime;

    /** 一审人 */
    private String oneReviewer;

    /** 一审备注 */
    private String oneReviewRemark;


    /** 订单状态*/
    //com.cloud.baowang.agent.api.enums.AgentReviewStatusEnum
    private Integer orderStatus;

    /** 锁单状态 0未锁 1已锁 */
    private Integer lockStatus;

    /** 锁单人 */
    private String locker;

    /** 二审开始时间 */
    private Long secondReviewStartTime;


    /** 二审完成时间 */
    private Long secondReviewFinishTime;

    /** 二审人 */
    private String secondReviewer;

    /** 二审备注 */
    private String secondReviewRemark;


    /** 调整负盈利佣金 */
    private BigDecimal adjustCommissionAmount;

    /** 负盈利佣金 */
    private String adjustCommissionRemark;

    /**
     * 订单最终态 - 驳回也存
     */
    private Integer finalStatus;


    /** 佣金申请金额 */
    private BigDecimal applyAmount;

}
