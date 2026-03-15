package com.cloud.baowang.play.game.cmd.response;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CmdCollusionReqVO {

    /**
     * 	用户名称
     */
    private String SourceName;
    /**
     * 投注金额
     */
    private BigDecimal Amt;
    /**
     * 客队ID
     */
    private Integer AwayId;
    /**
     * 投注时客队得分
     */
    private Integer AwayScore;

    /**
     * 投注位置
     */
    private String Choice;
    /**
     * 注单投注时间
     */
    private Long CreateTS;
    /**
     * 	货币代码
     */
    private String CurCode;
    /**
     * 注单状态
     * D: 正在处理的滚球赛事的单
     * N: 已接受今日或早盘的单
     * A: 已接受的滚球赛事的单
     * C: 已取消的单（一般为球赛取消造成）
     * R: 已拒绝的单
     */
    private String DangerStatus;

    /**
     * 会员货币转换为马币的汇率 (内部参数可忽略)
     */
    private BigDecimal ExRate;
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
    private BigDecimal Hdp;

    /**
     * 主队ID
     */
    private Integer HomeId;
    /**
     * 主队得分
     */
    private Integer HomeScore;

    /**
     * 半场得分
     */
    private String HTScore;

    /**
     * 是否为投注主队
     */
    private Boolean IsBetHome;
    /**
     * 是否为上半场
     */
    private Boolean IsFH;
    /**
     * 	是否为主队让球
     */
    private Boolean IsHomeGive;

    /**
     * 是否为滚球
     */
    private Boolean IsRun;

    /**
     * 联赛ID
     */
    private Integer LeagueId;

    /**
     * 注单赛事ID
     */
    private Integer MatchId;

    /**
     * 注单赛事系统ID
     * 若为VS注单 赛事ID为:"00000000-0000-0000-0000-000000000000"
     */
    private String MatchGroupId;
    /**
     * 投注赔率
     */
    private BigDecimal Odds;

    /**
     * 注单状态
     * D: 正在处理的滚球赛事的单
     * N: 已接受今日或早盘的单
     * A: 已接受的滚球赛事的单
     * C: 已取消的单（一般为球赛取消造成）
     * R: 已拒绝的单
     */
    private String ParDangerStatus;

    /**
     * 投注赔率
     */
    private BigDecimal ParOdds;

    /**
     * 输赢状态
     * WA = 全胜
     * WH = 赢一半
     * LA = 全输
     * LH = 输一半
     * D = 平局
     * P = 待定
     */
    private String ParStatus;

    /**
     * 玩法类型
     */
    private String ParTransType;
    /**
     * 注单
     */
    private String RefNo;

    /**
     * 预计赢多少
     */
    private BigDecimal RiskLose;
    /**
     * 预计输多少
     */
    private BigDecimal RiskWin;

    /**
     * 	数据 ID
     */
    private String SocTransId;

    /**
     * 	Parlay 细单数据 ID
     */
    private String SocTransParId;


    /**
     * SpecialId 可透过 API
     * LanguageInfo 查询其特别投注名.
     * SpecialId搭配中立场属性, 若中立
     * 场属性有重复开启的状况, 会出现以
     *15874(关闭中立场)
     * 15874,1000(二次开启中立场)
     * 15874,(二次关闭中立场)
     */
    private String SpecialId;

    /**
     * 运动项目
     */
    private String SportType;

    /**
     * 输赢状态
     * WA = 全胜
     * WH = 赢一半
     * LA = 全输
     * LH = 输一半
     * D = 平局
     * P = 待定
     */
    private String Status;

    /**
     * CMD用户下注时间, Ticks 数据
     */
    private Long TransDate;

    /**
     * 玩法类型
     */
    private String TransType;
    /**
     * 注单结算时间, 如赛事结束后又重启赛事的情形, 则注单结算时间以最后赛事结束时的注单结
     * 算时间为准.
     */
    private Long UpdateTS;

    /**
     * 投注金额
     */
    private BigDecimal WinAmt;

    /**
     * -1→全输
     * -0.5→输一半
     * 0→合
     * 0.5→赢一半
     * 1→全赢
     */
    private BigDecimal WinRate;
    /**
     * 注单结算时间, 如赛事结束后又重启赛事的情形, 则注单结算时间以最后赛事结束时的注单结
     * 算时间为准.
     */
    private Long StateUpdateTs;

    /**
     * 如果下注 AOS 则为不包括在 AOS 内的比分
     */
    private String AOSExcluding;
}
