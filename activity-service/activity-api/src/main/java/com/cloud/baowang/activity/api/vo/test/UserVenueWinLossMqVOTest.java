package com.cloud.baowang.activity.api.vo.test;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@Accessors(chain = true)
@Builder
@Schema(title = "会员场馆盈亏报表-MQ参数")
public class UserVenueWinLossMqVOTest {
    @Schema(description = "站点code")
    private String siteCode;
    @Schema(title = "日期-小时维度")
    private Long dayHour;
    @Schema(title = "用户账号")
    private String userAccount;
    @Schema(title = "用户ID")
    private String userId;
    @Schema(title = "币种")
    private String currency;
    @Schema(title = "代理ID")
    private String agentId;
    @Schema(title = "上级代理账号")
    private String agentAccount;
    @Schema(title = "游戏平台code")
    private String venueCode;
    @Schema(title = "游戏平台分类")
    private Integer venueType;
    @Schema(title = "平台游戏分类,不传则默认不分类")
    private String venueGameType;
    @Schema(title = "投注金额")
    private BigDecimal betAmount;
    @Schema(title = "有效投注")
    private BigDecimal validAmount;
    @Schema(title = "输赢")
    private BigDecimal winLossAmount;
    @Schema(title = "上一次结算时间 日期-小时维度")
    private Long lastDayHour;
    @Schema(title = "上一次投注额")
    private BigDecimal lastBetAmount;
    @Schema(title = "上一次有效投注")
    private BigDecimal lastValidBetAmount;
    @Schema(title = "上一次输赢")
    private BigDecimal lastBetWinLose;
    @Schema(title = "代理ID")
    private String lastAgentId;
    @Schema(title = "上级代理账号")
    private String lastAgentAccount;
    private Integer betCount;
    private String orderId;
    @Schema(title = "站点时区 消费者用")
    private String timeZone;

    public UserVenueWinLossMqVOTest() {
        this.venueCode = null;
        this.betCount = 0;
        this.dayHour = 0L;
        this.userAccount = null;
        this.agentAccount = null;
        this.betAmount = BigDecimal.ZERO;
        this.validAmount = BigDecimal.ZERO;
        this.winLossAmount = BigDecimal.ZERO;
        this.lastBetWinLose = BigDecimal.ZERO;
        this.lastValidBetAmount = BigDecimal.ZERO;
        this.lastBetAmount = BigDecimal.ZERO;
        this.orderId = orderId;
    }
}
