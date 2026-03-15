package com.cloud.baowang.play.game.shaba.response.client;

import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ClientBetDetail {

    /**
     * 注单号码
     */
    private String transId;
    /**
     * 厂商会员标识符
     */
    private String memberId;
    /**
     * 厂商账号
     */
    private String operatorId;
    /**
     * 联赛ID
     */
    private Integer leagueId;
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
     * 主赛事ID
     */
    private int parentId;
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
     * 系统开赛时间 (时区GMT+0)
     */
    private LocalDateTime kickOffTime;
    /**
     * 赛事开赛时间 (时区GMT+0)
     */
    private LocalDateTime globalShowTime;
    /**
     * 体育项目ID
     */
    private Integer sportType;
    /**
     * 体育项目名称
     */
    private String sportName;
    /**
     * 投注类型
     */
    private int betType;
    /**
     * 投注类型名称
     */
    private String betTypeName;
    /**
     * 串关票券编号
     */
    private Long parlayTicketNo;
    /**
     * 赔率
     */
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal price;
    /**
     * 下注金额
     */
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal stake;
    /**
     * 交易时间
     */
    private LocalDateTime transTime;
    /**
     * 注单状态
     */
    private String status;
    /**
     * 结算的赢利或亏损金额
     */
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal settlementPrice;
    /**
     * 币别ID
     */
    private int currency;
    /**
     * 盘口赔率类型
     */
    private int oddsType;
    /**
     * 盘口赔率类型名称
     */
    private String oddsTypeName;
    /**
     * 特殊投注类型时才显示信息
     */
    private String oddsInfo;
    /**
     * 投注类型选项
     */
    private String key;
    /**
     * 投注类型选项名称
     */
    private String keyName;
    /**
     * 特殊规则
     */
    private String excluding;
    /**
     * 是否为Lucky串关
     */
    private Boolean isLucky;
    /**
     * 串关类别
     */
    private String parlayType;
    /**
     * 串关组合
     */
    private String comboType;
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
     * 电子竞技游戏类型
     */
    private Integer eSportGameId;
    /**
     * 下注时主队得分
     */
    private Integer homeScore;
    /**
     * 下注时客队得分
     */
    private Integer awayScore;
    /**
     * 是否可以实时兑现
     */
    private boolean cashoutEnabled;
    /**
     * 是否已经实时兑现
     */
    private boolean alreadyCashout;
    /**
     * 实时兑现的金额
     */
    private BigDecimal cashoutPrice;
    /**
     * 串关信息列表
     */
    private List<ClientParlayInfo> parlayInfo;
    /**
     * 重新结算信息列表
     */
    private List<ClientResettlesInfo> resettles;
    /**
     * 投注类型中的 X 和 Y 值
     */
    private String resourceId;


}
