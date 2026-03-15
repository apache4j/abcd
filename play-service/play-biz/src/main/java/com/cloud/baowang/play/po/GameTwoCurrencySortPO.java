package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.*;

import java.io.Serializable;

/**
 * @author qiqi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("game_two_currency_sort")
public class GameTwoCurrencySortPO extends BasePO implements Serializable {

    /**
     * 站点CODE
     */
    private String siteCode;

    /**
     * 货币代码
     */
    private String currencyCode;

    /**
     * 游戏ID
     */
    private String gameId;


    /**
     * 游戏关联二级分类ID
     */
    private String gameJoinId;

    /**
     * 一级分类
     */
    private String gameOneId;

    /**
     * 二级分类ID
     */
    private String gameTwoId;

    /**
     * 排序
     */
    private Integer sort;


    /**
     * 一级分类-首页游戏-排序
     */
    private Integer gameOneHomeSort;

    /**
     * 首页- 一级分类-热门排序
     */
    private Integer gameOneHotSort;





}
