package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * @author sheldon
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("sport_events_recommend")
public class SportEventsRecommendPO extends BasePO {

    /**
     * 联赛_名称
     */
    private String leagueName;

    /**
     * 联赛_ID
     */
    private String leagueId;

    /**
     * 赛事CODE
     */
    private String eventsCode;

    /**
     * 主队ID
     */
    private String homeId;

    /**
     * 主队 名称
     */
    private String homeName;

    /**
     * 客队ID
     */
    private String awayId;

    /**
     * 客队名称
     */
    private String awayName;


    /**
     * 赛事ID
     */
    private String eventsId;


    /**
     * 体育项目ID.1: 足球。2: 篮球。3: 美式足球。4: 冰上曲棍球。9: 羽毛球。24: 手球。26: 橄榄球。43: 电子竞技
     */
    private Integer sportType;


    /**
     * 体育项目名称
     */
    private String sportName;


    @Schema(description = "球队名称")
    private String teamName;


    /**
     * 开赛时间
     */
    private Long startTime;

    /**
     * 结束时间
     */
    private Long endTime;


    /**
     * 原始报文
     */
    private String textInfo;




}
