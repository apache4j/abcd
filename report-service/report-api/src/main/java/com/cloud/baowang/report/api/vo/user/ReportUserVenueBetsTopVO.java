package com.cloud.baowang.report.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/09/10 9:52
 * @description: 用户排行榜对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户排行榜统计")
public class ReportUserVenueBetsTopVO {
    @Schema(description ="会员Id")
    private String userId;

    @Schema(description ="会员账号")
    private String userAccount;

    @Schema(description ="币种")
    private String currency;

    @Schema(description ="会员输赢")
    private BigDecimal winLossAmount = BigDecimal.ZERO;

    @Schema(description ="投注金额")
    private BigDecimal betAmount = BigDecimal.ZERO;

    @Schema(description ="有效投注-")
    private BigDecimal validAmount = BigDecimal.ZERO;


    @Schema(description ="会员输赢-平台币")
    private BigDecimal platWinLossAmount = BigDecimal.ZERO;

    @Schema(description ="投注金额-平台币")
    private BigDecimal platBetAmount = BigDecimal.ZERO;

    @Schema(description ="有效投注-平台币")
    private BigDecimal platValidAmount = BigDecimal.ZERO;
}
