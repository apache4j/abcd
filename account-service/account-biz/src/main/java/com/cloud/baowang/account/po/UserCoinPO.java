package com.cloud.baowang.account.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 会员中心钱包
 *
 * @author qiqi
 */
@Data
@TableName("user_coin")
public class UserCoinPO extends BasePO {

    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 会员ID
     */
    private String userId;

    /**
     * 会员ID
     */
    private String userAccount ;


    /**
     * 币种
     */
    private String currency;
    /**
     * 总金额
     */
    private BigDecimal totalAmount;
    /**
     * 冻结金额
     */
    private BigDecimal freezeAmount;
    /**
     * 可用余额
     */
    private BigDecimal availableAmount;
    /**
     * 是否自动带入场馆
     */
    private String isBringInVenue;

}
