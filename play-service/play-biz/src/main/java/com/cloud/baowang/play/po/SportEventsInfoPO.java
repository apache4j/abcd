package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.*;

/**
 * 体育联赛信息表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("sport_events_info")
public class SportEventsInfoPO extends BasePO {


    /**
     * 游戏场馆CODE
     */
    private String venueCode;

    /**
     * 体育项目ID:
     * 1: 足球，2: 篮球，3: 美式足球，4: 冰上曲棍球，
     * 9: 羽毛球，24: 手球，26: 橄榄球，43: 电子竞技
     */
    private Integer sportType;

    /**
     * 联赛ID
     */
    private String leagueId;

    /**
     * 时间戳
     */
    private String tstamp;

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
    private String leagueNameZhCn;

    /**
     * 联赛名称（繁體中文）
     */
    private String leagueNameCh;

    /**
     * 创建时间（毫秒时间戳）
     */
    private Long createdTime;

    /**
     * 更新时间（毫秒时间戳）
     */
    private Long updatedTime;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 更新人
     */
    private String updater;
}
