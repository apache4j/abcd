package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//每日竞赛机器人配置
public class ActivityDailyRobotAddVO {

    @Schema(description = "站点编号", hidden = true)
    private String siteCode;

    @Schema(description = "活动ID")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String activityId;

    @Schema(description = "竞赛-id")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String activityDailyCompetitionId;

    @Schema(description = "机器人账号")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String robotAccount;

//    @Schema(description = "机器人流水")
//    @NotNull(message = ConstantsCode.PARAM_ERROR)
//    private BigDecimal robotBetAmount;


    @Schema(description = "机器人初始化-投注流水")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal initRobotBetAmount;

    @Schema(description = "机器人流水最高阀值(流水WTC)")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal maxRobotBetAmount;

    @Schema(description = "流水增长百分比(%)")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal betGrowthPct;


}
