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
 * @Date 2/5/23 5:22 PM
 * @Version 1.0
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("site_vip_rank")
public class SiteVIPRankPO extends BasePO implements Serializable {

    /* VIP段位code */
    private Integer vipRankCode;

    /* 站点code */
    private String siteCode;

    /* VIP等级Codes */
    private String vipGradeCodes;

    /* VIP段位名称 */
    private String vipRankName;
    /**
     * 段位名称-i18code
     */
    private String vipRankNameI18nCode;

    /* 段位图标 */
    private String vipIcon;
    /**
     * 段位颜色
     */
    private String rankColor;

    /* 备注 */
    private String remark;
    /**
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     * 转盘是否参与(0:不参与,1:参与)
     */
    private Integer luckFlag;

    /* 转盘次数 */
    private BigDecimal luck;

    /**
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     * 周流水奖励是否参加
     */
    private Integer weekAmountFlag;

    /* 周流水奖励达成条件 */
    private BigDecimal weekAmountLimit;

    /* 周流水奖励比例1 */
    private BigDecimal weekAmountProp1;

    /* 周流水奖励比例2 */
    private BigDecimal weekAmountProp2;

    /* 周流水倍数 */
    private BigDecimal weekAmountMultiple;

    /**
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     * 月流水奖励是否参加
     */
    private Integer monthAmountFlag;

    /* 月流水奖励达成条件 */
    private BigDecimal monthAmountLimit;

    /* 月流水奖励比例1 */
    private BigDecimal monthAmountProp1;

    /* 月流水奖励比例2 */
    private BigDecimal monthAmountProp2;

    /* 月流水倍数 */
    private BigDecimal monthAmountMultiple;
    /**
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     * 是否参加周体育奖励
     */
    private Integer weekSportFlag;
    /**
     * {@link com.cloud.baowang.common.core.enums.YesOrNoEnum}
     * 是否有加密货币提款手续费
     */
    private Integer encryCoinFee;

    /* 是否有SVIP专属福利 */
    private Integer svipWelfare;

    /* 是否有豪华赠品 */
    private Integer luxuriousGifts;


    /**是否显示反水特权配置*/
    private Integer rebateConfig;

}
