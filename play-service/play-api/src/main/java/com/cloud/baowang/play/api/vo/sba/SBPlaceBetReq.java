package com.cloud.baowang.play.api.vo.sba;


import cn.hutool.core.util.ObjectUtil;
import com.cloud.baowang.play.api.enums.sb.SBCurrencyEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SBPlaceBetReq extends SBBaseReq {

    @Schema(title = "(string) PlaceBet")
    private String action;

    @Schema(title = "(string) 交易纪录 id")
    private String operationId;

    @Schema(title = "用户 id")
    private String userId;

    //SBCurrencyEnum
    @Schema(title = "沙巴体育货币币别 例如：1, 2, 20")
    private Integer currency;

    @Schema(title = "例如：35627959")
    private String matchId;

    @Schema(title = "例如：23, 24")
    private String homeId;

    @Schema(title = " 例如：23, 24")
    private String awayId;

    @Schema(title = "依据玩家的语系传入值。 例如：Chile (V)")
    private String homeName;

    @Schema(title = "依据玩家的语系传入值。例如：France (V)")
    private String awayName;

    @Schema(title = "赛事开始时间 (yyyy-MM-dd HH:mm:ss.SSS) GMT-4")
    private String kickOffTime;

    @Schema(title = "下注时间 (yyyy-MM-dd HH:mm:ss.SSS) GMT-4")
    private String betTime;

    @Schema(title = "注单金额")
    private BigDecimal betAmount;

    @Schema(title = "实际注单金额")
    private BigDecimal actualAmount;

    //SBSportTypeEnum
    @Schema(title = "例如：1, 2, 3")
    private Integer sportType;

    @Schema(title = "例如：1, 3")
    private Integer betType;

    @Schema(title = "依据玩家的语系传入值。 例如：Handicap")
    private String betTypeName;

    //SBOddsTypeEnum
    @Schema(title = "(short) 适用于 bettype = 8700(Player Tips)才会有值 例如：0,1, 2, 3, 4, 5")
    private String oddsType;

    @Schema(title = "(int) 例如：246903111")
    private Integer oddsId;

    @Schema(title = "例如：-0.95, 0.75")
    private BigDecimal odds;

    @Schema(title = "N (string) 依据玩家的语系传入值。例如：Over, 4-3")
    private String betChoice;

    @Schema(title = "N (string) betChoice 的英文语系名称。例如：Over, 4-3")
    private String betChoice_en;

    @Schema(title = "(string) 更新时间 (yyyy-MM-dd HH:mm:ss.SSS) GMT-4")
    private String updateTime;

    @Schema(title = "(int) 例如：152765")
    private Integer leagueId;

    @Schema(title = "(string)依据玩家的语系传入值。 例如：SABA INTERNATIONAL FRIENDLY Virtual PES 20 – 20 Mins Play")
    private String leagueName;

    @Schema(title = "(string) 联赛名称的英文语系名称。 E.g. SABA INTERNATIONAL FRIENDLY Virtual PES 20 – 20 Mins Play")
    private String leagueName_en;

    @Schema(title = "(String) 体育类型的英文语系名称。 E.g. Soccer")
    private String sportTypeName_en;

    @Schema(title = "(String) 投注类型的英文语系名称。 E.g. Handicap")
    private String betTypeName_en;

    @Schema(title = "(String) 主队名称的英文语系名称。E.g. Chile (V)")
    private String homeName_en;

    @Schema(title = "(String) 客队名称的英文语系名称。E.g. France (V)")
    private String awayName_en;

    @Schema(title = "(string) 例如：61.221.35.49 (IPV4)")
    private String IP;

    @Schema(title = "(boolean) 例如：true, false")
    private Boolean isLive;

    @Schema(title = "(string) 唯一 id.")
    private String refId;

    @Schema(title = "N (string) 选填，用户登入会话 id，由商户提供")
    private String tsId;

    @Schema(title = "N (string) 球头 在百练赛中(sporttype=161)表示下注时，前一颗的球号。")
    private String point;

    @Schema(title = "N (string) 球头 2 适用于 bettype = 646 才会有值, point = HDP, point2 = OU")
    private String Point2;

    @Schema(title = "N (string) 下注对象")
    private String betTeam;

    @Schema(title = "N (int) 下注时主队得分。 在百练赛中(sporttype=161)表示已开出大于 37.5 的球数,")
    private String homeScore;

    @Schema(title = "N (int) 下注时客队得分。 在百练赛中(sporttype=161)表示已开出小于 37.5 的球数, e.g. 1。")
    private String awayScore;

    @Schema(title = "(boolean) 会员是否为 BA 状态。 False:是 / true:否")
    private Boolean baStatus;

    @Schema(title = "N (string) 当 bet_team=aos 时,才返回此字段,返回的值代表会员投 注的正确比分不为列出的这些")
    private String excluding;

    //SBBetFromEnum
    @Schema(title = "(string)下注平台。")
    private String betFrom;

    @Schema(title = "(decimal) 需增加在玩家的金额。")
    private BigDecimal creditAmount;

    @Schema(title = "(decimal) 需从玩家扣除的金额。")
    private BigDecimal debitAmount;

    @Schema(title = "N (string) 适用于 bettype = 468,469 才会有值。")
    private String oddsInfo;

    @Schema(title = "(string) 开赛时间 (yyyy-MM-dd HH:mm:ss.SSS) GMT-4 提示: Outright Betting 的 matchDatetime 會是 KickOffTime.")
    private String matchDatetime;

    @Schema(title = "N (string) " +
            "-适用于 bettype = 9662~9667, 9676~9705 才会有值 " +
            "Bet type: 9662~9667 会显示 2 位球员名字 " +
            "Bet type: 9676~9705 会显示 1 位球员名字 " +
            "-适用于 bettype = 8700(Player Tips)才会有值 " +
            "例如: ss=20-(CN) , ss=21-(VN), ss=22-(TH)" +
            " ss 为 Streamer source 缩写 直播主代码: 22  语系: 泰文(TH)")
    private String betRemark;


    @Schema(title = "N (string)只有透过数据源下注(direct API 和 odds feed API)才会回传此 参数")
    private String vendorTransId;

    @Schema(title = "(decimal) 为 MMR 赔率类型提供的赔率 %。当 oddsType=6 时, 才返回。")
    private BigDecimal mmrPercentage;

    @Schema(title = "N (int) 优惠券類型 1: 零风险投注券")
    private Integer type;

    @Schema(title = "N (decimal) 优惠券額度")
    private BigDecimal quota;


    public boolean validate() {

        if (ObjectUtil.isEmpty(userId) ||
                ObjectUtil.isEmpty(refId) ||
                ObjectUtil.isEmpty(vendorTransId) ||
                ObjectUtil.isEmpty(betAmount) ||
                betAmount.compareTo(BigDecimal.ZERO) <= 0
                || ObjectUtil.isEmpty(currency)
        || ObjectUtil.isEmpty(SBCurrencyEnum.getSBCurrencyEnum(currency))) {
            return false;
        }

        return creditAmount.compareTo(BigDecimal.ZERO) <= 0 && debitAmount.compareTo(BigDecimal.ZERO) > 0;
    }


}

