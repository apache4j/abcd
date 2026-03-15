package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.activity.api.vo.redbag.RedBagRainRespVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
public class ActivityConfigRespVO {

    @Schema(description = "id")
    private String id;
    /**
     * 站点code
     */
    @Schema(title = "站点code", hidden = true)
    private String siteCode;

    @Schema(description = "活动请求模板")
    private String activityTemplate;

    @Schema(description = "首存活动-活动请求参数模板")
    private ActivityFirstRechargeRespVO activityFirstRechargeVO;

    @Schema(description = "次存-活动请求参数模板")
    private ActivitySecondRechargeRespVO activitySecondRechargeVO;

    @Schema(description = "周三免费旋转-活动请求参数模板")
    private ActivityFreeWheelRespVO activityFreeWheelVO;

    @Schema(description = "指定存款日期-活动请求参数模版")
    private ActivityAssignDayRespVO activityAssignDayVO;

    @Schema(description = "体育负盈利-活动请求参数模板")
    private ActivityLossInSportsRespVO activityLossInSportsVO;

    @Schema(description = "转盘-活动请求参数模板")
    private ActivitySpinWheelRespVO activitySpinWheelVO;

    @Schema(description = "红包雨-活动请求参数模板")
    private RedBagRainRespVO redBagRainVO;

    @Schema(description = "每日竞赛-活动请求参数模板")
    private ActivityDailyCompetitionRespVO activityDailyCompetitionVO;

    @Schema(description = "签到活动")
    private ActivityCheckInRespVO activityCheckInRespVO;

    @Schema(description = "静态活动")
    private ActivityBaseRespVO activityStaticRespVO;

}
