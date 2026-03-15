package com.cloud.baowang.play.api.vo.order.report;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Schema(description = "场馆盈亏重算返回vo")
public class VenueWinLoseRecalculateVO {
    /**
     * 日期小时维度
     */
    private Long dayHourMillis;
    /**
     * 站点日期 当天起始时间戳
     */
    private Long dayMillis;
    /**
     * 会员账号
     */
    private String userAccount;
    /**
     * 账号类型
     */
    private Integer accountType;
    /**
     * 会员Id
     */
    private String userId;
    /**
     * 上级代理Id
     */
    private String agentId;
    /**
     * 上级代理
     */
    private String agentAccount;
    /**
     * 游戏平台CODE
     */
    private String venueCode;

    /**
     * 平台分类
     */
    private Integer venueType;

    /**
     * 平台游戏分类
     */
    private String venueGameType;

    /**
     * 币种
     */
    private String currency;
    /**
     * 注单数
     */
    private Integer betCount;
    /**
     * 投注金额
     */
    private BigDecimal betAmount;
    /**
     * 有效投注
     */
    private BigDecimal validAmount;
    /**
     * 投注盈亏
     */
    private BigDecimal winLossAmount;
    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 适配视讯游戏id
     */
    private String roomType;
    /**
     * 打赏金额
     */
    private BigDecimal tipsAmount;

    private BigDecimal userWinLossAmount;
}
