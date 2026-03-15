package com.cloud.baowang.wallet.po;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * <p>
 * 保证金帐变记录
 * </p>
 *
 * @author ford
 * @since 2025-06-27
 */
@Getter
@Setter
@TableName("site_security_change_log")
public class SiteSecurityChangeLogPO extends BasePO {

    /**
     * '站点code'
     */
    private String siteCode;

    /**
     * ''站点名称''
     */
    private String siteName;

    /**
     * ''站点名称''
     */
    private String company;
    /**
     * '站点类型'
     */
    private Integer siteType;
    /**
     * '币种'
     */
    private String currency;
    /**
     * 资金账户类型
     */
    private String balanceAccount;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 来源订单号
     */
    private String sourceOrderNo;

    /**
     * 来源订单类型
     */
    private String sourceCoinType;
    /**
     * 帐号类型: user:会员 agent:代理 site:站点
     */
    private String userType;
    /**
     * 会员ID
     */
    private String userId;

    /**
     * 会员名称
     */
    private String userName;

    /**
     * 订单类型
     */
    private String coinType;

    /**
     * 收支类型 +:收入 -:支出
     */
    private String amountDirect;

    /**
     * '帐变前余额'
     */
    private BigDecimal beforeAmount;

    /**
     * '帐变金额'
     */
    private BigDecimal changeAmount;

    /**
     * 帐变后金额
     */
    private BigDecimal afterAmount;


    /**
     * 帐变时间
     */
    private Long changeTime;

    /**
     * 备注
     */
    private String memo;

}
