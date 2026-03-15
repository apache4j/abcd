package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description 兑换码兑换表
 * @author brence
 * @date 2025-10-27
 */
@Data
@NoArgsConstructor
@TableName("site_activity_redemption_code_exchange_info")
public class SiteActivityRedemptionCodeExchangePO extends BasePO {

    /**
     * 兑换码
     */
    private String code;

    /**
     * 兑换码类型，0:通用兑换码，1:唯一兑换码
     */
    private Integer category;

    /**
     * 兑换码币种
     */
    private String currency;

    /**
     * 兑换金额
     */
    private BigDecimal amount;

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 兑换码批次号
     */
    private String batchNo;

    /**
     * 兑换会员ID
     */
    private String userId;

}
