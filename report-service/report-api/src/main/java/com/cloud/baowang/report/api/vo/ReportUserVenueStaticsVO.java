package com.cloud.baowang.report.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


@Data
@Schema(title = "会员场馆投注信息")
public class ReportUserVenueStaticsVO {

    private String siteCode;
    @Schema(description ="会员Id")
    private String userId;
    @Schema(description ="会员账号")
    private String userAccount;

    @Schema(description = "agentId")
    private String agentId;
    /**
     * 站点日期 当天起始时间戳
     */
    @Schema(description = "当天起始时间戳")
    private Long dayMillis;
    @Schema(description = "场馆类型")
    private Integer venueType;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "场馆代码")
    private String venueCode;
    @Schema(description = "平台游戏代码")
    private String venueGameType;
    @Schema(description = "投注金额")
    private BigDecimal betAmount;
    @Schema(description = "有效投注")
    private BigDecimal validAmount;
    @Schema(description = "视讯游戏Id")
    private String roomType;



}
