package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * <p>
 * 保证金余额
 * </p>
 *
 * @author ford
 * @since 2025-06-27
 */
@Getter
@Setter
@TableName("site_security_balance")
public class SiteSecurityBalancePO extends BasePO {

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 站点名称
     */
    private String company;
    /**
     * 站点类型
     */
    private Integer siteType;
    /**
     * 币种
     */
    private String currency;
    /**
     * 可用金额
     */
    private BigDecimal availableBalance;

    /**
     * 冻结金额
     */
    private BigDecimal frozenBalance;

    /**
     * 预警阀值
     */
    private BigDecimal thresholdAmount;


    /**
     * 透支额度
     */
    private BigDecimal overdrawAmount;


    /**
     * 剩余透支额度
     */
    private BigDecimal remainOverdraw;

    /**
     * 冻结透支金额
     */
    private BigDecimal frozenOverdraw;

    /**
     * 保证金开启状态 0:未开启 1:已开启
     */
    private Integer securityStatus;

    /**
     * 保证金账户状态 1:正常 2:预警 3:透支
     */
    private Integer accountStatus;

}
