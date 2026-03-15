package com.cloud.baowang.play.game.shaba.response.client;

import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ClientParlayInfo {

    /**
     * Parlay注单号码
     */
    private long parlayId;
    /**
     * 联赛ID
     */
    private int leagueId;
    /**
     * 联赛名称
     */
    private String leagueName;
    /**
     * 联赛图片URL
     */
    private String leagueIconUrl;
    /**
     * 赛事ID
     */
    private int eventId;
    /**
     * 主队ID
     */
    private int homeTeamId;
    /**
     * 主队名称
     */
    private String homeTeamName;
    /**
     * 主队图片URL
     */
    private String homeIconUrl;
    /**
     * 客队ID
     */
    private int awayTeamId;
    /**
     * 客队名称
     */
    private String awayTeamName;
    /**
     * 客队图片URL
     */
    private String awayIconUrl;
    /**
     * 赛事开赛时间 (时区GMT+0)
     */
    private LocalDateTime kickOffTime;
    /**
     * 赛事开赛时间 (时区GMT+0)
     */
    private LocalDateTime globalShowTime;
    /**
     * 赔率
     */@JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal price;
    /**
     * 投注类型
     */
    private Integer betType;
    /**
     * 投注类型名称
     */
    private String betTypeName;
    /**
     * 投注类型选项
     */
    private String key;
    /**
     * 投注类型选项名称
     */
    private String keyName;
    /**
     * 体育项目ID
     */
    private Integer sportType;
    /**
     * 体育项目名称
     */
    private String sportName;
    /**
     * 主队让球
     */
    private BigDecimal homePoint;
    /**
     * 客队让球
     */
    private BigDecimal awayPoint;
    /**
     * 球头
     */
    private BigDecimal point;
    /**
     * 注单状态
     */
    private String status;
    /**
     * 下注时主队得分
     */
    private Integer homeScore;
    /**
     * 下注时客队得分
     */
    private Integer awayScore;
    /**
     * 投注类型中的 X 和 Y 值
     */
    private String resourceId;
}
