package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.serializer.SensitiveDataJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(title = "每日竞赛-计算出排名的对象")
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityRankingDailyVO implements Serializable {

    @Schema(description = "机器人ID", hidden = true)
    private String robotId;


    @Schema(description = "排名")
    private Integer ranking;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "账号")
    private String userAccount;

    @Schema(description = "优惠类型:字典CODE：activity_discount_type")
    private Integer activityDiscountType;

    @Schema(description = "法币投注币种")
    private String currencyCode;

    @Schema(description = "法币投注币种符号")
    private String betSymbol;

    @Schema(description = "法币-投注金额")
    private BigDecimal betAmount;

    @Schema(description = "投注金额-平台币")
    private BigDecimal platBetAmount;

    @Schema(description = "法币投注币种")
    private String platCurrencyCode;

    @Schema(description = "法币投注币种符号")
    private String platBetSymbol;

    @Schema(description = "奖金")
    private BigDecimal awardAmount;

    @Schema(description = "奖金币种")
    private String awardCurrencyCode;

    @Schema(description = "奖金币种符号")
    private String awardSymbol;

    @Schema(description = "true=机器人,false=真实用户", hidden = true)
    private Boolean type;

    @Schema(description = "彩金百分比")
    private BigDecimal activityAmountPer;

    @Schema(description = "奖金池金额")
    private BigDecimal totalAwardAmount;


    @Schema(description = "流水增长百分比")
    private BigDecimal betGrowthPct;

    @Schema(description = "是否编辑过（1是,0否）,这个字段每天晚上会重新恢复成0")
    private Boolean edit;


    @Schema(description = "初始化-投注金额(流水WTC)")
    private BigDecimal initRobotBetAmount;

    @Schema(description = "初始化-主货币流水")
    private BigDecimal initRobotCurrencyBetAmount;


    @Schema(description = "机器人流水最高阀值")
    private BigDecimal maxRobotBetAmount;

}
