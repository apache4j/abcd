package com.cloud.baowang.play.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.BasePO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("game_two_class_info")
public class GameTwoClassInfoPO extends BasePO {

    /**
     * 分类名称
     */
    private String typeName;

    /**
     * 分类名称-多语言
     */
    private String typeI18nCode;

    /**
     * 站点CODE
     */
    private String siteCode;


    /**
     * 排序
     */
    private Integer sort;


    /**
     * 一级分类ID
     */
    private String gameOneId;


    /**
     * 模板CODE
     */
    private String modelCode;

    /**
     * 状态（ 状态:1:开启中,2:维护中,3:已禁用)
     */
    private Integer status;

    /**
     * 图标
     */
    private String icon;

    /**
     * 皮肤4:横图-多语言
     */
    private String htIconI18nCode;

    @Schema(description = "冠名标签")
    private Integer siteLabelChangeType;


}
