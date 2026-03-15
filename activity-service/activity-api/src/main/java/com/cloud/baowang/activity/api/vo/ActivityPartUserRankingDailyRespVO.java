package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.cloud.baowang.common.core.serializer.SensitiveDataJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Schema(title = "每日竞赛-活动排名用户")
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityPartUserRankingDailyRespVO {

    @Schema(description = "机器人ID", hidden = true)
    private String robotId;


    @Schema(description = "排名")
    private Integer ranking;

    @Schema(description = "账号")
    @JsonSerialize(using = SensitiveDataJsonSerializer.class)
    private String userAccount;

    @Schema(description = "投注金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal betAmount;

    @Schema(description = "平台币-投注金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal platBetAmount;

    @Schema(description = "奖金")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal awardAmount;

    @Schema(description = "true=机器人,false=真实用户", hidden = true)
    private Boolean type;

    @Schema(description = "true=是当前用户,高亮显示,false=不需要高亮显示")
    private Boolean specialShow;

    @Schema(description = "下注币种符号")
    private String betCurrencySymbol;

    @Schema(description = "下注币种")
    private String betCurrencyCode;

    @Schema(description = "奖励币种符号")
    private String awardCurrencySymbol;

    @Schema(description = "奖励币种")
    private String awardCurrencyCode;


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

    public BigDecimal getBetAmount() {
        return betAmount==null?BigDecimal.ZERO:this.betAmount;
    }

    public BigDecimal getPlatBetAmount() {
        return platBetAmount==null?BigDecimal.ZERO:this.platBetAmount;
    }

    public BigDecimal getAwardAmount() {
        return awardAmount==null?BigDecimal.ZERO:this.awardAmount;
    }
}
