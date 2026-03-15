package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("game_one_float_config")
public class GameOneFloatConfigPO extends BasePO {

    private String siteCode;

    /**
     * 一级分类
     */
    private String gameOneId;

    /**
     * 二级分类
     */
    private String gameTwoId;

    /**
     * 场馆
     */
    private String venueCode;

    /**
     * 悬浮名称-多语言
     */
    private String floatNameI18nCode;


    /**
     * 品牌图标-多语言
     */
    private String logoIconI18nCode;



    /**
     * 中图标-多语言
     */
    private String mediumIconI18nCode;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 一级分类的类型,单场馆.多游戏.
     */
    private String model;


}
