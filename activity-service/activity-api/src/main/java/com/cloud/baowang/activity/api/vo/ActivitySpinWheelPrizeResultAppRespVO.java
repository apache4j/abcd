package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 转盘活动返回活动详情
 */
@Data
@Schema(title = "转盘活动返回活动详情-app")
@I18nClass
public class ActivitySpinWheelPrizeResultAppRespVO  {


    /**
     * 是否抽中
     */
    @Schema(description = "是否抽中")
    private Boolean isReward;



    @Schema(description = "转盘活动中奖详情")
    private SiteActivityRewardSpinAPPResponseVO rewardSpinWheelConfig;



}