package com.cloud.baowang.play.game.cmd.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CmdResponseVO {

    /**
     * 下注单号
     */
    private String Id;
    /**
     * 	用户名称
     */
    private String SourceName;
    /**
     * 注单
     */
    private String ReferenceNo;
    /**
     * 	注单系统ID
     */
    private String SocTransId;
    /**
     * 	是否是上半场
     */
    private Boolean IsFirstHalf;
    /**
     * CMD用户下注时间, Ticks 数据
     */
    private Long TransDate;
    /**
     * 	是否为主队让球
     */
    private Boolean IsHomeGive;
    /**
     * 是否为投注主队
     */
    private Boolean IsBetHome;
    /**
     * 	注单下注金额
     */
    private BigDecimal BetAmount;
    /**
     * 	用户未结算金额
     */
    private BigDecimal Outstanding;

    /**
     * 	让球数
     */
    private BigDecimal Hdp;
    /**
     * 投注赔率
     */
    private BigDecimal Odds;
    /**
     * 	货币代码
     */
    private String Currency;
    /**
     * 	输赢金额
     */
    private BigDecimal WinAmount;
    /**
     * 会员货币转换为马币的汇率 (内部参数可忽略)
     */
    private BigDecimal ExchangeRate;
    /**
     * 输赢状态
     * WA = 全胜
     * WH = 赢一半
     * LA = 全输
     * LH = 输一半
     * D = 平局
     * P = 待定
     */
    private String WinLoseStatus;

    /**
     * 玩法类型
     */
    private String TransType;

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
     * 会员所得佣金
     */
    private BigDecimal MemCommission;
    /**
     * 玩家投注时IP
     */
    private String BetIp;

    /**
     * 主队得分
     */
    private Integer HomeScore;

    /**
     * 客队得分
     */
    private Integer AwayScore;

    /**
     * 投注时主队得分
     */
    private Integer RunHomeScore;

    /**
     * 投注时客队得分
     */
    private Integer RunAwayScore;


    /**
     * 是否为滚球
     */
     private Boolean IsRunning;
    /**
    * 注单拒绝原因
     */
    private String RejectReason;
    /**
     * 运动项目
     */
    private String SportType;
    /**
     * 投注位置
     */
    private String Choice;

    /**
     * 注单赛事工作日
     */
    private Integer WorkingDate;

    /**
     * MY: 马来盘
     * ID: 印度尼西亚盘
     * HK: 香港盘
     * DE: 欧洲盘
     * US: 美国盘
     */
    private String OddsType;

    /**
     * 球赛开赛日期
     */
    private Long MatchDate;

    /**
     * 主队ID
     */
    private Integer HomeTeamId;
    /**
     * 客队ID
     */
    private Integer AwayTeamId;
    /**
     * 联赛ID
     */
    private Integer LeagueId;
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
     * 否重算标识（>=2 标识该单有重算）
     */
    private Integer StatusChange;

    /**
     * 注单结算时间, 如赛事结束后又重启赛事的情形, 则注单结算时间以最后赛事结束时的注单结
     * 算时间为准.
     */
    private Long StateUpdateTs;

    /**
     * 佣金设定值
     */
    private BigDecimal MemCommissionSet;

    /**
     * 注单是否卖单
     */
    private Boolean IsCashOut;

    /**
     * 注单卖单数量
     */
    private BigDecimal CashOutTotal;


    /**
     * 注单卖单所得金额
     */
    private BigDecimal CashOutTakeBack;


    /**
     * 注单卖单输赢值
     */
    private BigDecimal CashOutWinLoseAmount;

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
    private Integer BetSource;


    /**
     * 如果下注 AOS 则为不包括在AOS 内的比分.
     */
    private String AOSExcluding;

    /**
     * (内部参数可忽略)
     */
    private BigDecimal MMRPercent;
    /**
     * 注单赛事ID
     */
    private Integer MatchID;

    /**
     * 注单赛事系统ID
     * 若为VS注单 赛事ID为:"00000000-0000-0000-0000-000000000000"
     */
    private String MatchGroupID;
    /**
     * OddsTrader 系统专用值. (可忽略)
     */
    private String BetRemarks;

    /**
     *是否是特别投注
     */
    private Boolean IsSpecial;


    /**
     *  串关List详情
      */
    private List<CmdCollusionReqVO> collusionBetDetails;
}
