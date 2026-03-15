package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.io.Serializable;

/**
 * 商务审核信息
 */
@TableName("agent_merchant_review_record")
@Data
public class AgentMerchantReviewRecordPO extends BasePO implements Serializable {

    /**
     * siteCode
     */
    private String siteCode;

    /**
     * 商务账号
     */
    private String merchantAccount;


    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 商务名称
     */
    private String merchantName;

    /**
     * 申请人
     */
    private String applicant;

    /**
     * 申请时间
     */
    private Long applicationTime;

    /**
     * 申请备注
     */
    private String applicationRemark;

    /**
     * 审核状态,通system_param review_status
     * {@link com.cloud.baowang.common.core.enums.ReviewStatusEnum}
     */
    private Integer reviewStatus;

    /**
     * 审核操作(归集状态,同system_param review_operation)
     * {@link com.cloud.baowang.common.core.enums.ReviewOperationEnum}
     */
    private Integer reviewOperation;

    /**
     * 锁单人
     */
    private String locker;

    /**
     * 锁单时间
     */
    private Long lockTime;

    /**
     * 锁单状态
     * {@link com.cloud.baowang.common.core.enums.LockStatusEnum}
     */
    private Integer lockStatus;

    /**
     * 审核人
     */
    private String auditName;

    /**
     * 审核时间
     */
    private Long auditTime;
    /**
     * 审核备注
     */
    private String auditRemark;
    /**
     * 申请密码
     */
    private String merchantPassword;

}
