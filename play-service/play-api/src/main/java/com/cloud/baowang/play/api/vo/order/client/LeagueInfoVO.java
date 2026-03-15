package com.cloud.baowang.play.api.vo.order.client;

import lombok.Data;

/**
 * 联赛信息实体
 */
@Data
public class LeagueInfoVO {

    /**
     * 联赛ID
     */
    private String leagueId;

    /**
     * 时间戳
     */
    private Long tstamp;

    /**
     * 联赛名称（英文）
     */
    private String leagueNameEn;

    /**
     * 联赛名称（日文）
     */
    private String leagueNameJp;

    /**
     * 联赛名称（捷克语）
     */
    private String leagueNameCs;

    /**
     * 联赛名称（泰语）
     */
    private String leagueNameTh;

    /**
     * 联赛名称（韩语）
     */
    private String leagueNameKo;

    /**
     * 联赛名称（越南语）
     */
    private String leagueNameVn;

    /**
     * 联赛名称（简体中文）
     */
    private String leagueNameZhcn;

    /**
     * 联赛名称（繁體中文）
     */
    private String leagueNameCh;

    /**
     * 体育类型（如足球、篮球、板球等）
     */
    private Integer sportType;
}
