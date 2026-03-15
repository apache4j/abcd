package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("site_vip_rank_currency_config")
public class SiteVipRankCurrencyConfigPO extends BasePO {
    private String siteCode;
    private Integer vipRankCode;
    private String currencyCode;
    private Integer dailyWithdrawals;
    private BigDecimal dayWithdrawLimit;
    private BigDecimal withdrawFee;
    private Integer withdrawFeeType;
    private String withdrawWayId;

    /**
     * 单日提款次数上限
     */
    private Integer dailyWithdrawalNumsLimit;
    /**
     *  单日提款额度上限
     */
    private BigDecimal dailyWithdrawAmountLimit;
}
