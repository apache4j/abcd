package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @className: SiteActivityRewardSpinWheelPO
 * @author: wade
 * @description: 转盘活动返回活动详情
 * @date: 10/9/24 17:37
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@I18nClass
@Schema(title = "转盘活动返回活动详情-app-配置的奖励")
public class SiteActivityRewardSpinAPPResponseVO implements Serializable {


    /**
     * 站点code
     */
    private String siteCode;
    /**
     * 奖励等级 0	青铜
     * 1	白银
     * 2	黄金及以上
     */
    @Schema(description = "vip段位")
    private Integer rewardRank;

    /**
     * 奖励等级 0	青铜
     * 1	白银
     * 2	黄金及以上
     */
    @Schema(description = "vip段位名称")
    private String rewardRankName;

    /**
     * 奖品等级
     */
    @Schema(description = "奖品等级")
    private Integer prizeLevel;

    /**
     * 奖品类型
     */
    @Schema(description = "奖品类型")
    private String prizeType;

    @Schema(description = "奖品类型名称")
    private String prizeTypeName;

    /**
     * 奖品名称
     */
    @Schema(description = "奖品名称")
    private String prizeName;

    /**
     * 奖品价值
     */
    @Schema(description = "奖品价值")
    private BigDecimal prizeAmount;

    /**
     * 奖品展示图
     */
    @Schema(description = "奖品展示图")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String prizePictureUrl;

    /**
     * 奖品展示图
     */
    @Schema(description = "奖品展示图")
    private String prizePictureUrlFileUrl;


    /**
     * 活动概率
     */
    /*@Schema(description = "活动概率",hidden = true)
    private BigDecimal probability;*/

    /**
     * 活动id
     */
    private String baseId;

    /**
     * 奖项配置id
     */
    @Schema(description = "奖项配置id")
    private String id;



}