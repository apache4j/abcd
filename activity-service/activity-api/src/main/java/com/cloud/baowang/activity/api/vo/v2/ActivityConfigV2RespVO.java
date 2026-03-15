package com.cloud.baowang.activity.api.vo.v2;

import com.cloud.baowang.activity.api.vo.*;
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
public class ActivityConfigV2RespVO {

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
    private ActivityFirstRechargeV2RespVO activityFirstRechargeVO;

    @Schema(description = "次存-活动请求参数模板")
    private ActivitySecondRechargeV2RespVO activitySecondRechargeVO;


    @Schema(description = "指定存款日期-活动请求参数模版")
    private ActivityAssignDayV2RespVO activityAssignDayVO;

    @Schema(description = "赛事包赔-活动请求参数模版")
    private ActivityContestPayoutV2RespVO activityContestPayoutRespVO;

    @Schema(description = "静态活动")
    private ActivityBaseV2RespVO activityStaticRespVO;

    @Schema(description = "新手活动")
    private ActivityNewHandRespVO activityNewHandRespVO;


}
