package com.cloud.baowang.play.game.cmd.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CmdWebResponseVO {

    /**
     * 下注单号
     */
    private String id;
    /**
     * 	用户名称
     */
    private String sourceName;
    /**
     * 注单
     */
    private String referenceNo;
    /**
     * 	注单系统ID
     */
    private String socTransId;
    /**
     * 	是否是上半场
     */
    private Boolean isFirstHalf;
    /**
     * CMD用户下注时间, Ticks 数据
     */
    private Long transDate;
    /**
     * 	是否为主队让球
     */
    private Boolean isHomeGive;
    /**
     * 是否为投注主队
     */
    private Boolean isBetHome;
    /**
     * 	注单下注金额
     */
    private BigDecimal betAmount;
    /**
     * 	用户未结算金额
     */
    private BigDecimal outstanding;

    /**
     * 	让球数
     */
    private BigDecimal hdp;
    /**
     * 投注赔率
     */
    private BigDecimal odds;
    /**
     * 	货币代码
     */
    private String currency;
    /**
     * 	输赢金额
     */
    private BigDecimal winAmount;
    /**
     * 会员货币转换为马币的汇率 (内部参数可忽略)
     */
    private BigDecimal exchangeRate;
    /**
     * 输赢状态
     * WA = 全胜
     * WH = 赢一半
     * LA = 全输
     * LH = 输一半
     * D = 平局
     * P = 待定
     */
    private String winLoseStatus;

    /**
     * 玩法类型
     */
    private String transType;

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
     * 会员所得佣金
     */
    private BigDecimal memCommission;
    /**
     * 玩家投注时IP
     */
    private String betIp;

    /**
     * 主队得分
     */
    private Integer homeScore;

    /**
     * 客队得分
     */
    private Integer awayScore;

    /**
     * 投注时主队得分
     */
    private Integer runHomeScore;

    /**
     * 投注时客队得分
     */
    private Integer runAwayScore;


    /**
     * 是否为滚球
     */
     private Boolean isRunning;
    /**
    * 注单拒绝原因
     */
    private String rejectReason;
    /**
     * 运动项目
     */
    private String sportType;
    /**
     * 投注位置
     */
    private String choice;

    /**
     * 注单赛事工作日
     */
    private Integer workingDate;

    /**
     * MY: 马来盘
     * ID: 印度尼西亚盘
     * HK: 香港盘
     * DE: 欧洲盘
     * US: 美国盘
     */
    private String oddsType;

    /**
     * 球赛开赛日期
     */
    private Long matchDate;

    /**
     * 主队ID
     */
    private Integer homeTeamId;
    /**
     * 客队ID
     */
    private Integer awayTeamId;
    /**
     * 联赛ID
     */
    private Integer leagueId;
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
     * 否重算标识（>=2 标识该单有重算）
     */
    private Integer statusChange;

    /**
     * 注单结算时间, 如赛事结束后又重启赛事的情形, 则注单结算时间以最后赛事结束时的注单结
     * 算时间为准.
     */
    private Long stateUpdateTs;

    /**
     * 佣金设定值
     */
    private BigDecimal memCommissionSet;

    /**
     * 注单是否卖单
     */
    private Boolean isCashOut;

    /**
     * 注单卖单数量
     */
    private BigDecimal cashOutTotal;


    /**
     * 注单卖单所得金额
     */
    private BigDecimal cashOutTakeBack;


    /**
     * 注单卖单输赢值
     */
    private BigDecimal cashOutWinLoseAmount;

    /**
     * 下注平台
     * 1 = Desktop
     * 7 = Mobile(v1)
     * 9 = New Mobile(v1)
     * 10 = Mobile E-Sport(v3)
     * 11 = New Mobile E-Sport(v3)
     * 12 = ES PC网
     * 14 = VS PC网
     * 15 = VS PC 亚洲版
     * 16 = VS 手机网
     */
    private Integer betSource;


    /**
     * 如果下注 AOS 则为不包括在AOS 内的比分.
     */
    private BigDecimal aOSExcluding;

    /**
     * (内部参数可忽略)
     */
    private BigDecimal MMRPercent;
    /**
     * 注单赛事ID
     */
    private Integer matchID;

    /**
     * 注单赛事系统ID
     * 若为VS注单 赛事ID为:"00000000-0000-0000-0000-000000000000"
     */
    private String matchGroupID;
    /**
     * OddsTrader 系统专用值. (可忽略)
     */
    private String betRemarks;

    /**
     *是否是特别投注
     */
    private Boolean isSpecial;


    /**
     *  串关List详情
      */
    private List<CmdWebCollusionReqVO> collusionBetDetails;
}
