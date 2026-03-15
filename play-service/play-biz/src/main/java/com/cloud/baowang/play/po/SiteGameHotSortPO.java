package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.*;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("site_game_hot_sort")
public class SiteGameHotSortPO extends BasePO implements Serializable {

    private String siteCode;

    /**
     * 站点-游戏ID site_game.id
     */
    private String siteGameId;

    /**
     * 游戏ID
     */
    private String gameId;

    /**
     * 支持的币种
     */
    private String currencyCode;


    /**
     * 首页 - 热门排序
     */
    private Long homeHotSort;






}
