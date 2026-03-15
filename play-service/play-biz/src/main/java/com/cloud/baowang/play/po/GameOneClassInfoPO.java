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
@TableName("game_one_class_info")
public class GameOneClassInfoPO extends BasePO {

    /**
     * 目录名称
     */
    private String directoryName;

    /**
     * 目录名称-多语言
     */
    private String directoryI18nCode;

    /**
     * 首页名称
     */
    private String homeName;


    /**
     * 首页名称-多语言
     */
    private String homeI18nCode;

    /**
     * 站点CODE
     */
    private String siteCode;


    /**
     * 状态（ 状态:1:开启中,2:维护中,3:已禁用)
     */
    private Integer status;


    /**
     * 图片
     */
    private String icon;


    /**
     * 图片
     */
    private String icon2;


    /**
     * 多语言图片CODE
     */
    private String typeIconI18nCode;


    /**
     * 一级分类模板,GameOneTypeEnum
     */
    private String model;

    /**
     * 皮肤4:国内盘字段:奖金池
     */
    private BigDecimal prizePoolTotal;

    /**
     * 皮肤4:国内盘字段:奖金池开始金额
     */
    private BigDecimal prizePoolStart;

    /**
     * 皮肤4:国内盘字段:奖金池结束金额
     */
    private BigDecimal prizePoolEnd;

    /**
     * 皮肤4:国内盘字段:返水场馆类型标签
     */
    private Integer rebateVenueType;

    /**
     * 场馆ID
     */
    private String venueId;

    private String venueCode;
}
