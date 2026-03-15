package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(title = "每日竞赛")
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityPartDailyCompletionRespVO {

    private String id;

    @Schema(description = "true=活动详情页面,false=活动空页面")
    private Boolean type;

    @Schema(description = "总奖池")
    private BigDecimal totalRewardsAmount;

    @Schema(description = "奖池-币种符号")
    private String currencySymbol;

    @Schema(description = "活动名称")
    @I18nField
    private String activityName;

    @Schema(description = "场馆CODE",hidden = true)
    private String venueCode;


    @Schema(description = "秒")
    private Long second;


    @Schema(description = "今日活动时间")
    private String time;

    @Schema(description = "活动规则")
    @I18nField
    private String activityRule;


    @Schema(description = "上届冠军用户信息")
    private ActivityPartDailyPreviousRespVO previous;

    @Schema(description = "当前用户信息")
    private ActivityPartDailyCompletionUserRespVO user;

    @Schema(description = "今日排名信息")
    private List<ActivityPartUserRankingDailyRespVO> list;

}
