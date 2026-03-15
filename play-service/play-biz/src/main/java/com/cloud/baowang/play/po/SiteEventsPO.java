package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.*;

/**
 * 站点-体育联赛排序
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("site_events")
public class SiteEventsPO extends BasePO {


    /**
     * 站点
     */
    private String siteCode;

    /**
     * 游戏场馆CODE
     */
    private String venueCode;

    /**
     * sport_events_info.id
     */
    private String eventsInfoId;

    /**
     * 联赛ID
     */
    private String leagueId;


    /**
     * 排序
     */
    private Long sort;

    /**
     * 球类
     */
    private Integer sportType;

}
