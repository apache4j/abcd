package com.cloud.baowang.play.game.shaba.response;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * 注单详情实体类
 */
@Data
public class BetDetail {
    /**
     * 注单识别码
     */
    private String trans_id;

    /**
     * 厂商会员识别码
     */
    private String vendor_member_id;

    /**
     * 厂商ID或子网站名称
     */
    private String operator_id;

    /**
     * 联盟识别码
     */
    private Integer league_id;

    /**
     * 联盟名称，当league_id=null时，隐藏栏位
     */
    private List<LangName> leaguename;

    /**
     * 赛事编号，当BetType = 10时，不会返回此字段. 当Sport type: 202/220/222时, 则为Game No
     */
    private Integer match_id;

    /**
     * 队伍识别码，当BetType = 10时，显示栏位
     */
    private Integer team_id;

    private String game_id; // 场次编号，当 sport_type = 169、175 时，显示字段

    /**
     * 主队识别码，当BetType = 10时，隐藏栏位
     */
    private Integer home_id;

    /**
     * 主队名称
     */
    private List<LangName> hometeamname;

    /**
     * 客队识别码，当BetType = 10时，隐藏栏位
     */
    private Integer away_id;

    /**
     * 客队名称
     */
    private List<LangName> awayteamname;

    /**
     * 比赛开球时间
     */
    private String match_datetime;

    /**
     * 体育种类。请参考附件"体育种类表" 当BetType = 29和2799时, sport type返回null.
     * SBSportTypeEnum.java
     */
    private Integer sport_type;

    /**
     * 体育种类名称
     */
    private List<LangName> sportname;

    /**
     * 下注类型。请参考附件"下注类型表"
     */
    private Integer bet_type;

    /**
     * 下注类型名称
     */
    private List<LangName> bettypename;

    /**
     * 混合过关注单号码，使用此号码于GetSystemParlayDetail取得混合过关注单内容
     */
    private Long parlay_ref_no;

    /**
     * 注单赔率
     */
    private Double odds;

    /**
     * 会员投注金额
     */
    private BigDecimal stake;

    /**
     * For Keno,Index range
     */
    private Integer range;

    /**
     * 投注交易时间
     */
    private String transaction_time;

    /**
     * 注单状态
     */
    private String ticket_status;


    private BigDecimal buyback_amount;

    /**
     * 此注输或赢的金额
     */
    private BigDecimal winlost_amount;

    /**
     * 下注后的余额
     */
    private BigDecimal after_amount;

    /**
     * 为此会员设置币别。请参考附件"币别表"
     */
    private Integer currency;

    /**
     * 决胜时间(仅显示日期),请依此字段做为后台报表对帐使用.
     */
    private String winlost_datetime;

    /**
     * 赔率类型。请参考附件中"赔率类型表"，当sport_type=245、168或bet_type=468、469 返回字段odds_type=0
     */
    private Integer odds_type;

    /**
     * 当 bettype 为 468 或 469, 此字段则显示.
     */
    private String odds_Info;

    /**
     * 彩票游戏类别 (请参考下注类型资讯)
     */
    private String lottery_bettype;

    /**
     * 下注选项内容.
     */
    private String bet_team;

    /**
     * 下注选项名称
     */
    private List<LangName> betteamname;

    /**
     * Game No 虚拟娱乐城才会出现此字段.
     */
    private String game_no;

    /**
     * 当bet_team=aos时,才返回此字段,返回的值代表会员投注的正确比分不为列出的这些.
     */
    private String exculding;

    /**
     * 表示来至Lucky下注 For Parlay, 值=true/ false
     */
    private String isLucky;

    /**
     * For Parlay, 值=Mix Parlay/ System Parlay
     */
    private String parlay_type;

    /**
     * For Parlay, 值= Doubles/Trebles/Trixie/Lucky 7/4-Fold/Yankee/Lucky 15/5-Fold/Canadian/Lucky 31/6-Fold/Heinz/Lucky 63/7-Fold/Super Heinz/Lucky 127/8-Fold/Goliath/Lucky 255/9-Fold/10-Fold/11-Fold/12-Fold/13-Fold/14-Fold/15-Fold/16-Fold/17-Fold/18-Fold/19-Fold/20-Fold
     */
    private String combo_type;

    /**
     * X 与 Y 的值。请参考附件”下注类型表” 例) bettype 1311 - Set X Winner 當 bet_type = 228 或 229, 此欄位代表投注的选定时段 例) bet_tag : 61 代表選定時段為 00:00 - 61:00
     */
    private String bet_tag;

    /**
     * 主队让球
     */
    private Double home_hdp;

    /**
     * 客队让球
     */
    private Double away_hdp;

    /**
     * 让球
     */
    private Double hdp;

    /**
     * over/under
     */
    private Double ou_hdp;

    /**
     * 下注平台表。请参考附件中"下注平台表"
     */
    private String betfrom;

    /**
     * 比赛是否为live, 1: 是/ 0: 否
     */
    private String islive;

    /**
     * 下注时主队得分
     */
    private Integer home_score;

    /**
     * 下注时客队得分
     */
    private Integer away_score;

    /**
     * 注单结算的时间,仅支援 sport_type:1~99,175, 当 ticket_status=void 时, 此字段非最终结算时间. 当 ticket_status=reject 时, 则不支持此字段.
     */
    private String settlement_time;

    /**
     * 厂商备注
     */
    private String customInfo1;

    /**
     * 厂商备注
     */
    private String customInfo2;

    /**
     * 厂商备注
     */
    private String customInfo3;

    /**
     * 厂商备注
     */
    private String customInfo4;

    /**
     * 厂商备注
     */
    private String customInfo5;

    /**
     * 不支持此功能. 会员是否为 BA 状态 1: 是 0: 否
     */
    private String ba_status;

    /**
     * 版本号
     */
    private Long version_key;

    /**
     * 电子竞技游戏类型. 当 sport_type=43 返回字段. 请参考电子竞技游戏代码表
     */
    private Integer esports_gameid;

    /**
     * 下列 bet type 才会显示此字段. Bettype:9662~9667 會顯示 2 位球员名字 Bettype:9676~9695,9712~9721,9730~9739,9696~9699,9702~9705 會顯示1位球员名字 Bettype:8700 将显示如下, ss: 直播主語系 中文: 20-(cn)、越南文: 21-(vn)、泰文: 22-(th)
     */
    private String bet_remark;

    /**
     * 总进球. 当 bet_type=228 返回字段. 注单未结算时返回数值为 0, 结算后返回投注预测时间的赛果.
     */
    private Integer total_score;

    /**
     * 玩家下注当下的风险等级. 分级详请请参考沙巴后台>玩家风险等级. 仅支援 sport_type:1~99 当 bet_type=38 时,则不显示此字段.
     */
    private String risklevelname;

    /**
     * 玩家投注当下的风险等级(中文名称). 当 bet_type=38 时,则不显示此字段.
     */
    private String risklevelnamecs;

    /**
     * 缅甸盘赔率的%.当 oddstype=6 时,显示字段.
     */
    private Integer mmr_percentage;

    /**
     *
     */
    private List<ResettLementInfo> resettlementinfo;

    /**
     * 只有 saba.club free bet 才有的欄位, 值=free bet, 表示是用 free bet 下注的注单.
     */
    private String tokenmoney;

    /**
     * 折扣比例. 此字段仅在沙巴虚拟财神爷串关注单返回. 例如: percentage: 0.1 代表折扣 10%
     */
    private Double percentage;

    /**
     * 折扣后的投注金额.
     */
    private Double discount_stake;

    /**
     * "cashout" : 表示该张注单已被实时兑现
     */
    private String ticket_extra_status;

    /**
     * 混合过关资料
     */
    private List<ParlayData> parlayData;

    /**
     * SingleParlayData
     */
    private List<SingleParlayData> singleParlayData;

    // 原始的json数据
    private String originalParlayInfo;

    //区分虚拟赛事的类型.
    private String game_group;

}







