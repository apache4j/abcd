package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>
 * 证金调整审核表
 * </p>
 *
 * @author ford
 * @since 2025-06-27
 */
@Getter
@Setter
@TableName("site_security_adjust_review")
public class SiteSecurityAdjustReviewPO extends BasePO {

    /**
     * '站点code'
     */
    private String siteCode;

    /**
     * siteName
     */
    private String siteName;

    /**
     * 申请时间
     */
    private Long applyTime;
    /**
     * 一审完成时间
     */
    private Long firstReviewTime;

    /**
     * 审核单号
     */
    private String reviewOrderNumber;

    /**
     * 审核操作
     */
    private Integer reviewOperation;


    /**
     * 审核状态
     */
    private Integer reviewStatus;



    /**
     * 申请人
     */
    private String applyUser;


    /**
     * 一审人
     */
    private String firstReviewer;

    /**
     * 锁单状态 1锁单 0 解锁
     */
    private Integer lockStatus;

    /**
     * 调整类型
     */
    private Integer adjustType;

    /**
     * 调整金额
     */
    private BigDecimal adjustAmount;

    /**
     * 锁单人
     */
    private String locker;


    /**
     * 一审完成备注
     */
    private String reviewRemark;

    /**
     * 币种
     */
    private String currency;

    /**
     * 申请原因
     */
    private String remark;

    /**
     * 锁定时间
     */
    private String lockTime;

}
