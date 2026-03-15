package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SiteActivityDailyCompetitionDetail implements Serializable {

    @Schema(description = "排名")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer ranking;

    @Schema(description = "彩金百分比")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal activityAmountPer;

    @Schema(description = "彩金金额")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal activityAmount;

    @Schema(description = "免费旋转次数")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Integer freeTimes;

    /**
     * 游戏场馆
     */
    @Schema(description = "游戏场馆")
    private String venueCode;

    /**
     * pp游戏code
     */
    @Schema(description = "pp游戏code")
    private String accessParameters;

    /**
     * 限注金额
     */
    @Schema(description = "限注金额")
    private BigDecimal betLimitAmount;


}
