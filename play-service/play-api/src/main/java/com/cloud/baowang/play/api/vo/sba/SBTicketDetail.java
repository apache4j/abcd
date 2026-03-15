package com.cloud.baowang.play.api.vo.sba;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SBTicketDetail {


    /** 例如：35627959 */
    private int matchId;

    /** 例如：23, 24 */
    private int homeId;

    /** 例如：23, 24 */
    private int awayId;

    /** 依据玩家的语系传入值。例如：Chile (V) */
    private String homeName;

    /** 依据玩家的语系传入值。例如：France (V) */
    private String awayName;

    /** 赛事开始时间 (yyyy-MM-dd HH:mm:ss.SSS) GMT-4 */
    private String kickOffTime;

    /** 例如：1, 2, 3 */
    private int sportType;

    /** 依据玩家的语系传入值。例如：Soccer */
    private String sportTypeName;

    /** 例如：1, 3 */
    private int betType;

    /** 依据玩家的语系传入值。例如：Handicap */
    private String betTypeName;

    /** 例如：246903111 */
    private int oddsId;

    /** 例如： -0.95, 0.75 */
    private BigDecimal odds;

    /** 例如：1, 2, 3, 4, 5 */
    private short oddsType;

    /** 依据玩家的语系传入值。例如：Over, 4-3 */
    private String betChoice;

    /** betChoice 的英文语系名称。例如：Over, 4-3 */
    private String betChoice_en;

    /** 例如： 152765 */
    private int leagueId;

    /** 依据玩家的语系传入值。例如：SABA INTERNATIONAL FRIENDLY Virtual PES 20 - 20 Mins Play */
    private String leagueName;

    /** 会员是否为 BA 状态。 false:是 / true:否 */
    private boolean isLive;

    /** 球头 */
    private String point;

    /** 球头 2 */
    private String point2;

    /** 下注对象 */
    private String betTeam;

    /** 下注时主队得分 */
    private int homeScore;

    /** 下注时客队得分 */
    private int awayScore;

    /** 当 bet_team=aos 时,才返回此字段,返回的值代表会员投注的正确比分不为列出的这些 */
    private String excluding;

    /** 联赛名称的英文语系名称。 e.g. SABA INTERNATIONAL FRIENDLY Virtual PES 20 - 20 Mins Play */
    private String leagueName_en;

    /** 体育类型的英文语系名称。 e.g. Soccer */
    private String sportTypeName_en;

    /** 主队名称的英文语系名称。e.g. Chile (V) */
    private String homeName_en;

    /** 客队名称的英文语系名称。e.g. France (V) */
    private String awayName_en;

    /** 投注类型的英文语系名称。 e.g. Handicap */
    private String betTypeName_en;

    /** 开赛时间 (yyyy-MM-dd HH:mm:ss.SSS) GMT-4 */
    private String matchDatetime;

    /** 适用于 bettype = 9662~9667, 9676~9705 才会有值 */
    private String betRemark;

}
