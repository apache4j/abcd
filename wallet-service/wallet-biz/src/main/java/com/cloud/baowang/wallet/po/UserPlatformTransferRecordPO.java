package com.cloud.baowang.wallet.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员平台币钱包
 *
 * @author qiqi
 */
@Data
@TableName("user_platform_transfer_record")
public class UserPlatformTransferRecordPO extends BasePO {

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 订单时间
     */
    private Long orderTime;

    /**
     * 会员ID
     */
    private String userId;

    /**
     * 会员账号
     */
    private String userAccount ;

    /**
     * 代理id
     */
    private String agentId ;

    /**
     * 代理账号
     */
    private String agentAccount ;

    /**
     * 平台币币种
     */
    private String platCurrencyCode;

    /**
     * 转换币种 用户法币
     */
    private String targetCurrencyCode;

    /**
     * 转换金额
     */
    private BigDecimal transferAmount;

    /**
     * 汇率
     */
    private BigDecimal transferRate;
    /**
     * 目标金额
     */
    private BigDecimal targetAmount;



}
