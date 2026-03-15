package com.cloud.baowang.play.game.cmd.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CmdWebCollusionReqVO {

    /**
     * 	用户名称
     */
    private String sourceName;
    /**
     * 投注金额
     */
    private BigDecimal amt;
    /**
     * 客队ID
     */
    private Integer awayId;
    /**
     * 投注时客队得分
     */
    private Integer awayScore;

    /**
     * 投注位置
     */
    private String choice;
    /**
     * 注单投注时间
     */
    private Long createTS;
    /**
     * 	货币代码
     */
    private String curCode;
    /**
     * 注单状态
     * D: 正在处理的滚球赛事的单
     * N: 已接受今日或早盘的单
     * A: 已接受的滚球赛事的单
     * C: 已取消的单（一般为球赛取消造成）
     * R: 已拒绝的单
     */
    private String dangerStatus;

    /**
     * 会员货币转换为马币的汇率 (内部参数可忽略)
     */
    private BigDecimal exRate;
    /**
     * 全场得分
     */
    private String FTScore;

    /**
     * 进球情况
     * 1: 主队最先进球, 主队最后进球
     * 2: 主队最先进球, 客队最后进球
     * 3: 客队最先进球, 主队最后进球
     * 4: 客队最先进球, 客队最后进球
     * 5: 没有进球
     */
     private Integer HAG;
    /**
     * 让球数
     */
    private BigDecimal hdp;

    /**
     * 主队ID
     */
    private Integer homeId;
    /**
     * 主队得分
     */
    private Integer homeScore;

    /**
     * 半场得分
     */
    private String HTScore;

    /**
     * 是否为投注主队
     */
    private Boolean isBetHome;
    /**
     * 是否为上半场
     */
    private Boolean isFH;
    /**
     * 	是否为主队让球
     */
    private Boolean isHomeGive;

    /**
     * 是否为滚球
     */
    private Boolean isRun;

    /**
     * 联赛ID
     */
    private Integer leagueId;

    /**
     * 注单赛事ID
     */
    private Integer matchId;

    /**
     * 注单赛事系统ID
     * 若为VS注单 赛事ID为:"00000000-0000-0000-0000-000000000000"
     */
    private String matchGroupId;
    /**
     * 投注赔率
     */
    private BigDecimal odds;

    /**
     * 注单状态
     * D: 正在处理的滚球赛事的单
     * N: 已接受今日或早盘的单
     * A: 已接受的滚球赛事的单
     * C: 已取消的单（一般为球赛取消造成）
     * R: 已拒绝的单
     */
    private String parDangerStatus;

    /**
     * 投注赔率
     */
    private BigDecimal parOdds;

    /**
     * 输赢状态
     * WA = 全胜
     * WH = 赢一半
     * LA = 全输
     * LH = 输一半
     * D = 平局
     * P = 待定
     */
    private String parStatus;

    /**
     * 玩法类型
     */
    private String parTransType;
    /**
     * 注单
     */
    private String refNo;

    /**
     * 预计赢多少
     */
    private BigDecimal riskLose;
    /**
     * 预计输多少
     */
    private BigDecimal riskWin;

    /**
     * 	数据 ID
     */
    private String socTransId;

    /**
     * 	Parlay 细单数据 ID
     */
    private String socTransParId;


    /**
     * SpecialId 可透过 API
     * LanguageInfo 查询其特别投注名.
     * SpecialId搭配中立场属性, 若中立
     * 场属性有重复开启的状况, 会出现以
     *15874(关闭中立场)
     * 15874,1000(二次开启中立场)
     * 15874,(二次关闭中立场)
     */
    private String specialId;

    /**
     * 运动项目
     */
    private String sportType;

    /**
     * 输赢状态
     * WA = 全胜
     * WH = 赢一半
     * LA = 全输
     * LH = 输一半
     * D = 平局
     * P = 待定
     */
    private String status;

    /**
     * CMD用户下注时间, Ticks 数据
     */
    private Long transDate;

    /**
     * 玩法类型
     */
    private String transType;
    /**
     * 注单结算时间, 如赛事结束后又重启赛事的情形, 则注单结算时间以最后赛事结束时的注单结
     * 算时间为准.
     */
    private Long updateTS;

    /**
     * 投注金额
     */
    private BigDecimal winAmt;

    /**
     * -1→全输
     * -0.5→输一半
     * 0→合
     * 0.5→赢一半
     * 1→全赢
     */
    private BigDecimal winRate;
    /**
     * 注单结算时间, 如赛事结束后又重启赛事的情形, 则注单结算时间以最后赛事结束时的注单结
     * 算时间为准.
     */
    private Long stateUpdateTs;

    /**
     * 如果下注 AOS 则为不包括在 AOS 内的比分
     */
    private String AOSExcluding;
}
