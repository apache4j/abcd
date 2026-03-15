package com.cloud.baowang.play.api.vo.tf;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TfOrderInfoVO {

    /**
     * 注单号
     */
    @JsonProperty("id")
    private String id;

    /**
     * 下注选项
     */
    @JsonProperty("bet_selection")
    private String betSelection;

    /**
     * 赔率
     * Note: If it is combo, OR, 1x2, SPOR, it will be euro odds. Else it is malay odds
     * 备注：如果是连串, OR, 1x2, SPOR，赔率是欧盘赔率。否则是马来赔率。
     */
    @JsonProperty("odds")
    private BigDecimal odds;

    /**
     * 货币
     */
    @JsonProperty("currency")
    private String currency;

    /**
     * 下注金额
     */
    @JsonProperty("amount")
    private BigDecimal amount;

    /**
     * 游戏名称
     */
    @JsonProperty("game_type_name")
    private String gameTypeName;

    /**
     * 盘口名称
     */
    @JsonProperty("game_market_name")
    private String gameMarketName;

    /**
     * 盘口局分
     *  match = 总局
     *  map = 局
     */
    @JsonProperty("market_option")
    private String marketOption;

    /**
     * 第几局
     * MAP 1 = 第一局
     * Q1 = 第一节
     * R1 = 第一局
     * FIRST HALF - 上半场
     * SECOND HALF - 下半场
     */
    @JsonProperty("map_num")
    private String mapNum;

    /**
     * 盘口类型
     * WIN = 主盘口独赢 (下注选项: home/away)
     * 1X2 = 独赢 (下注选项: home/draw/away)
     * AH = 让分局 (下注选项: home/away)
     * OU = 大小 (下注选项: over/under)
     * OE = 单双 (下注选项: odd/even)
     * SPWINMAP = 局独赢 (下注选项: home/away)
     * WINMAP = 局独赢比分 (下注选项: home/away)
     * SPHA = 特别主客 (下注选项: home/away)
     * SPYN = 特别是否 (下注选项: yes/no)
     * SPOE = 特别单双 (下注选项: odd/even)
     * SPOU = 特别大小 (下注选项: over/under)
     * SP1X2 = 特别1X2 (下注选项: home/draw/away)
     * OR = 冠军盘 (下注选项: 队伍名字)
     * SPOR = 特别多项 (下注选项: 自定)
     * SPXX = 特别双项 (下注选项: 自定)
     * SPMOR = Special Proposition Multi Outright
     * SPOEU = Special Proposition Over Equal Under
     * SPMM = Special Proposition Min Max
     * SPRLE = Special Proposition Range Less Than Equal
     * SP777 = Special Proposition 777
     * SPAD = Special Proposition Attack Defend
     */
    @JsonProperty("bet_type_name")
    private String betTypeName;

    /**
     * 比赛名称
     */
    @JsonProperty("competition_name")
    private String competitionName;

    /**
     * 赛事ID
     */
    @JsonProperty("event_id")
    private Integer eventId;

    /**
     * 赛事名称
     */
    @JsonProperty("event_name")
    private String eventName;

    /**
     * 赛事开始时间
     * format 2019-07-13T15:40:00Z, UTC+0
     */
    @JsonProperty("event_datetime")
    private String eventDateTime;

    /**
     * 下注时间
     * format 2019-07-13T15:40:00Z, UTC+0
     */
    @JsonProperty("date_created")
    private String dateCreated;

    /**
     * 结算时间
     * format 2019-07-13T15:40:00Z, UTC+0
     */
    @JsonProperty("settlement_datetime")
    private String settlementDateTime;

    /**
     * 更改时间
     * format 2019-07-13T15:40:00Z, UTC+0
     */
    @JsonProperty("modified_datetime")
    private String modifiedDateTime;

    /**
     * 注单状况
     * confirmed = 确定
     * settled = 结算
     * cancelled = 取消
     */
    @JsonProperty("settlement_status")
    private String settlementStatus;

    /**
     * 盘口结果
     */
    @JsonProperty("result")
    private String result;

    /**
     * 注单结果
     * win = 赢
     * loss = 输
     * draw = 和
     * cancelled = 取消
     */
    @JsonProperty("result_status")
    private String resultStatus;

    /**
     * 输赢额
     * win (赢) = 本金 (bet amount) + 盈利 (winnings)
     * loss (输) = -本金 (-bet amount)
     * draw (和) = 本金 (bet amount)
     * pending (未结算) = null
     * cancelled (取消) = null
     */
    @JsonProperty("earnings")
    private BigDecimal earnings;

    /**
     * 让分数
     */
    @JsonProperty("handicap")
    private BigDecimal handicap;

    /**
     * 是否连串
     */
    @JsonProperty("is_combo")
    private boolean isCombo;

    /**
     * 会员号
     */
    @JsonProperty("member_code")
    private String memberCode;

    /**
     * true = 已重新结算
     */
    @JsonProperty("is_unsettled")
    private boolean isUnsettled;

    /**
     * 注单下注状况
     * db = 早盘
     * live = 滚球
     */
    @JsonProperty("ticket_type")
    private String ticketType;

    /**
     * 马来赔率
     * Note: if it is combo, it will be null
     * 备注：如果是连串，赔率会是null
     */
    @JsonProperty("malay_odds")
    private BigDecimal malayOdds;

    /**
     * 欧盘赔率
     */
    @JsonProperty("euro_odds")
    private BigDecimal euroOdds;

    /**
     * 会员下的赔率
     */
    @JsonProperty("member_odds")
    private BigDecimal memberOdds;

    /**
     * 会员下的盘
     * euro
     * hongkong
     * indo
     * malay
     */
    @JsonProperty("member_odds_style")
    private String memberOddsStyle;

    /**
     * 游戏ID
     */
    @JsonProperty("game_type_id")
    private int gameTypeId;

    /**
     * 下注的管道
     * desktop-browser = 电脑浏览器
     * mobile-browser = 手机浏览器 (包括嵌入在APP里)
     * mobile-app = 手机APP
     * unknown = 未知
     * null = 旧数据没有
     */
    @JsonProperty("request_source")
    private String requestSource;

    private BigDecimal memberWinLossF;

    private List<TfOrderInfoVO> tickets;

}
