package com.cloud.baowang.play.game.shaba.response;


import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


/**
 * 混合过关资料实体类
 */
@Data
public class ParlayData {
    /**
     * 混合过关资料唯一识别码
     */
    private Long parlay_id;

    /**
     * 联盟识别码
     */
    private Integer league_id;

    /**
     * 赛事编号
     */
    private Integer match_id;

    /**
     * 主队识别码
     */
    private Integer home_id;

    /**
     * 客队识别码
     */
    private Integer away_id;

    /**
     * 比赛开球时间
     */
    private String match_datetime;

    /**
     * 注单赔率
     */
    private BigDecimal odds;

    /**
     * 下注类型
     */
    private Integer bet_type;

    /**
     * 下注选项内容
     */
    private String bet_team;

    /**
     * 体育种类
     */
    private Integer sport_type;

    /**
     * 主队让球
     */
    private BigDecimal home_hdp;

    /**
     * 客队让球
     */
    private BigDecimal away_hdp;

    /**
     * 让球
     */
    private BigDecimal hdp;


    /**
     * 是否为live
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
     * 注单状态
     */
    private String ticket_status;

    /**
     * 决胜时间
     */
    private String winlost_datetime;

    /**
     * 下注类型 (英文)
     */
    private String selection_name;

    /**
     * 下注类型 (简中)
     */
    private String selection_name_cs;

    /**
     * 注单状态 ※won/lose/void/running/draw/reject/refund
     */
    private String status;

    /**
     * 联赛信息
     **/
    private List<LangName> leaguename;

    /**
     * 主队信息
     **/
    private List<LangName> hometeamname;

    /**
     * 客队信息
     **/
    private List<LangName> awayteamname;

    /**
     * 玩法
     **/
    private List<LangName> bettypename;

    private String bet_tag;





}