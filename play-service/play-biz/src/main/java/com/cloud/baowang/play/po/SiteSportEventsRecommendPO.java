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
@TableName("site_sport_events_recommend")
public class SiteSportEventsRecommendPO extends BasePO {

//
    /**
     * 站点CODE
     */
    private String siteCode;

    /**
     * 游戏场馆CODE
     */
    private String venueCode;

    /**
     * sport_events_recommend.id
     */
    private String sportRecommendId;

    /**
     * 联赛ID
     */
    private String leagueId;

    /**
     * 赛事ID
     */
    private String eventsId;

    /**
     * 体育项目ID:1: 足球。2: 篮球。3: 美式足球。4: 冰上曲棍球。9: 羽毛球。24: 手球。26: 橄榄球。43: 电子竞技
     */
    private Integer sportType;




}
