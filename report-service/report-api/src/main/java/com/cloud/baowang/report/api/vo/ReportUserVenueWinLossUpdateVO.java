package com.cloud.baowang.report.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Builder
@Schema(title = "会员每日场馆盈亏报表更新VO")
public class ReportUserVenueWinLossUpdateVO {
    @Schema(description = "站点code")
    private String siteCode;
    @Schema(title = "会员账号")
    private String userAccount;
    @Schema(title = "会员ID")
    private String userId;
    @Schema(title = "上级代理")
    private String agentAccount;
    @Schema(title = "上级代理id")
    private String agentId;
    @Schema(title = "游戏平台code")
    private String venueCode;
    @Schema(title = "游戏名称")
    private String gameName;
    @Schema(title = "投注金额")
    private BigDecimal betAmount;
    @Schema(title = "有效投注")
    private BigDecimal validAmount;
    @Schema(title = "投注盈亏")
    private BigDecimal winLossAmount;
    @Schema(title = "日期小时维度")
    private Long dayHour;
    @Schema(title = "平台游戏分类")
    private String venueGameType;
    @Schema(title = "注单数量")
    private Integer betCount;
}
