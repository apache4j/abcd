package com.cloud.baowang.activity.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "site_activity_reward_spin_wheel")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SiteActivityRewardSpinWheelPO extends SiteBasePO implements Serializable {



    /**
     * 站点code
     */
    private String siteCode;
    /**
     * 奖励等级 0	青铜
     * 1	白银
     * 2	黄金及以上
     */
    private Integer rewardRank;

    /**
     * 奖品等级
     */
    private Integer prizeLevel;

    /**
     * 奖品类型
     */
    private Integer prizeType;

    /**
     * 奖品名称
     */
    private String prizeName;

    /**
     * 奖品价值
     */
    private BigDecimal prizeAmount;

    /**
     * 奖品展示图
     */
    private String prizePictureUrl;

    /**
     * 活动概率
     */
    private BigDecimal probability;

    /**
     * 活动id
     */
    private String baseId;


}