package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className: SiteActivityRewardVipGradePO
 * @author: wade
 * @description:
 * @date: 7/9/24 15:39
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "site_activity_reward_vip_grade")
public class SiteActivityRewardVipGradePO extends SiteBasePO {
    /**
     * 站点code
     */
    private String siteCode;

    /**
     * VIP等级code
     */
    private Integer vipGradeCode;

    /**
     * VIP等级名称
     */
    private String vipGradeName;

    /**
     * VIP段位code
     */
    private Integer vipRankCode;

    /**
     * vip段位名称
     */
    private String vipRankName;

    /**
     * 段位名称-多语言
     */
    private String vipRankNameI18nCode;

    /**
     * 活动模板
     */
    private String activityTemplate;

    /**
     * 活动id
     */
    private String baseId;

    /**
     * 领取次数
     */
    private Integer rewardCount;


}
