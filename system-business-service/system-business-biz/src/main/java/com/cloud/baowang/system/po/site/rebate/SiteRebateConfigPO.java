package com.cloud.baowang.system.po.site.rebate;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("site_rebate_config")
public class SiteRebateConfigPO extends BasePO implements Serializable {
    public SiteRebateConfigPO(String siteCode) {
        this.siteCode = siteCode;
    }

    @TableField(updateStrategy = FieldStrategy.NEVER)
    private String siteCode;

    private String currencyCode;

    @TableField(updateStrategy = FieldStrategy.NEVER)//vipGradeCode
    private String vipGradeCode;

    @TableField(updateStrategy = FieldStrategy.NEVER)//vipGradeName
    private String vipGradeName;



    private BigDecimal sportsRebate;

    private BigDecimal esportsRebate;

    private BigDecimal videoRebate;

    private BigDecimal pokerRebate;

    private BigDecimal slotsRebate;

    private BigDecimal lotteryRebate;

    private BigDecimal cockfightingRebate;

    private BigDecimal fishingRebate;

    private BigDecimal dailyLimit;

    private BigDecimal marblesRebate;

    private Integer status;

}
