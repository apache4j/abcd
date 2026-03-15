package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.activity.api.vo.redbag.RedBagRainVO;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityConfigVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "操作人",hidden = true)
    private String operator;
    /**
     * 站点code
     */
    @Schema(title = "站点code",hidden = true)
    private String siteCode;

    @Schema(description = "活动请求模板 "+
            "红包雨:RED_BAG_RAIN, " +
            "首存活动:FIRST_DEPOSIT, " +
            "次存:SECOND_DEPOSIT," +
            " 免费旋转:FREE_WHEEL, " +
            "指定日期存款:ASSIGN_DAY, " +
            "体育负盈利:LOSS_IN_SPORTS, " +
            "流水排行榜:TURNOVER_RANKING, " +
            "每日竞赛:DAILY_COMPETITION:" +
            "转盘:SPIN_WHEEL")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String activityTemplate;

    @Schema(description = "体育负盈利-活动请求参数模板")
    private ActivityLossInSportsVO activityLossInSportsVO;

    @Schema(description = "每日竞赛-活动请求参数模板")
    private ActivityDailyCompetitionVO activityDailyCompetitionVO;

    @Schema(description = "首存活动-活动请求参数模板")
    private ActivityFirstRechargeVO activityFirstRechargeVO;

    @Schema(description = "周三免费旋转-活动请求参数模板")
    private ActivityFreeWheelVO activityFreeWheelVO;

    @Schema(description = "指定存款日期-活动请求参数模版")
    private ActivityAssignDayVO activityAssignDayVO;

    @Schema(description = "次存-活动请求参数模板")
    private ActivitySecondRechargeVO activitySecondRechargeVO;

    @Schema(description = "红包雨-活动请求参数模板")
    private RedBagRainVO redBagRainVO;

    @Schema(description = "转盘-活动请求参数模板")
    private ActivitySpinWheelVO activitySpinWheelVO;

    @Schema(description = "签到-活动请求参数模板")
    private ActivityCheckInVO activityCheckInVO;

    @Schema(description = "静态-活动请求参数模板")
    private ActivityBaseVO staticActivityInVO;

}
