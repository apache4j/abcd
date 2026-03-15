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
 * @Author : 小智
 * @Date : 2024/8/28 16:10
 * @Version : 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("site_vip_sport")
public class SiteVipSportPO extends BasePO implements Serializable {

    private String siteCode;

    private Long rankId;

    private Integer rankCode;

    /* 周体育场馆流水 */
    private BigDecimal weekSportBetAmount;

    /* 周体育场馆奖金 */
    private BigDecimal weekSportBonus;

    /* 周体育流水倍数 */
    private BigDecimal weekSportMultiple;

}
