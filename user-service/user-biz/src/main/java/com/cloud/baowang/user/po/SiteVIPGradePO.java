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
 * @Date : 2024/8/2 13:56
 * @Version : 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("site_vip_grade")
public class SiteVIPGradePO extends BasePO implements Serializable {

    /* 站点code */
    private String siteCode;

    /* VIP等级code */
    private Integer vipGradeCode;

    /* VIP等级名称 */
    private String vipGradeName;

    /* VIP段位code */
    private Integer vipRankCode;

    /* 升级条件所需XP */
    private BigDecimal upgradeXp;

    /* 升级奖金 */
    private BigDecimal upgradeBonus;

    /* 图标地址 */
    private String picIcon;
}
