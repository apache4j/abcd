package com.cloud.baowang.activity.api.vo.v2;

import com.cloud.baowang.activity.api.vo.*;
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
public class ActivityConfigV2VO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "操作人",hidden = true)
    private String operator;

    @Schema(title = "站点code",hidden = true)
    private String siteCode;

    @Schema(description = "活动请求模板 首存活动:FIRST_DEPOSIT_V2, 次存:SECOND_DEPOSIT_V2,指定日期存款:ASSIGN_DAY_V2,赛事包赔:CONTEST_PAYOUT_V2")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String activityTemplate;

    @Schema(description = "首存活动-活动请求参数模板")
    private ActivityFirstRechargeV2VO activityFirstRechargeVO;

    @Schema(description = "指定存款日期-活动请求参数模版")
    private ActivityAssignDayV2VO activityAssignDayVO;

    @Schema(description = "次存-活动请求参数模板")
    private ActivitySecondRechargeV2VO activitySecondRechargeVO;

    @Schema(description = "静态-活动请求参数模板")
    private ActivityBaseV2VO staticActivityInVO;

    @Schema(description = "新手-活动请求参数模板")
    private ActivityNewHandVO activityNewHandVO;

    @Schema(description = "赛事包赔-活动请求参数模板")
    private ActivityContestPayoutV2VO activityContestPayoutVO;
    @Schema(title = "状态 0已禁用 1开启中")
    private Integer status;


}
