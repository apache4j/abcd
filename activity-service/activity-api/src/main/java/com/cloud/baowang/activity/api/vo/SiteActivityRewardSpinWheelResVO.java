package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.activity.api.enums.ActivityRewardRankEnum;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(description = "转盘奖励配置返回")
public class SiteActivityRewardSpinWheelResVO implements Serializable {


    /**
     * 站点code
     */
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;


    /**
     * activity_reward_rank
     * {@link  ActivityRewardRankEnum}
     * 奖励等级 0	青铜
     * 1	白银
     * 2	黄金及以上
     */
    @Schema(description = "1-青铜 2-白银 3-黄金及以上 system-param(activity_reward_rank)")
    private Integer rewardRank;
    @Schema(description = "奖品等级")
    private Integer prizeLevel;

    /**
     * activity_reward_type
     */
    @Schema(description = "奖品类型,1-金额 2-实物 system-param(activity_reward_type)")
    private Integer prizeType;

    @Schema(description = "奖品名称")
    private String prizeName;

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


    @Schema(description = "中奖概率，数字类型，1-100")
    private BigDecimal probability;

    /**
     * 活动id
     */
    @Schema(description = "baseId", hidden = true)
    private String baseId;


}