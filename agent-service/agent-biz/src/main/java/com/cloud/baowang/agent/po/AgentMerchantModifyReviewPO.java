package com.cloud.baowang.agent.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

@Data
@TableName("agent_merchant_modify_review")
public class AgentMerchantModifyReviewPO extends BasePO {

    /**
     * 站点编码
     */
    private String siteCode;

    /**
     * 商务账号
     */
    private String merchantAccount;
    /**
     * 商务姓名
     */
    private String merchantName;

    /**
     * 申请时间
     */
    private Long applicationTime;

    /**
     * 一审完成时间
     */
    private Long firstReviewTime;

    /**
     * 审核单号
     */
    private String reviewOrderNumber;

    /**
     * 审核操作 system_param review_operation值
     */
    private Integer reviewOperation;

    /**
     * 审核状态 system_param review_status值
     */
    private Integer reviewStatus;

    /**
     * 申请人
     */
    private String applicant;

    /**
     * 一审人
     */
    private String firstInstance;

    /**
     * 锁单状态 1锁单 0解锁
     */
    private Integer lockStatus;

    /**
     * 审核申请类型
     */
    private Integer reviewApplicationType;

    /**
     * 修改前
     */
    private String beforeFixing;

    /**
     * 修改后
     */
    private String afterModification;

    /**
     * 锁单人
     */
    private String locker;

    /**
     * 一审完成备注
     */
    private String reviewRemark;

    /**
     * 申请信息
     */
    private String applicationInformation;

}
