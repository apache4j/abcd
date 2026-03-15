package com.cloud.baowang.user.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author 小智
 * @Date 4/5/23 11:17 AM
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("site_vip_benefit")
public class SiteVIPBenefitPO extends BasePO implements Serializable {

    /* 站点code */
    private String siteCode;

    private Integer vipGradeCode;

    /* 每周返还奖金比例(%) */
    private BigDecimal weekRebate;

    private BigDecimal weekMinBetAmount;

    /* 周流水倍数 */
    private BigDecimal weekBetMultiple;

    /* 每月返还奖金比例(%) */
    private BigDecimal monthRebate;

    private BigDecimal monthMinBetAmount;
    /* 月流水倍数 */
    private BigDecimal monthBetMultiple;

    /* 周体育最低流水 */
    private BigDecimal weekSportMinBet;

    /* 周体育倍数 */
    private BigDecimal weekSportMultiple;

    /* 周体育奖金 */
    private BigDecimal weekSportRebate;


    /* 升级奖金 */
    private BigDecimal upgrade;

    /* 幸运转盘 */
    private Integer luckTime;
}
