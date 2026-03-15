package com.cloud.baowang.play.api.vo.lobby;

import com.alibaba.fastjson2.JSONObject;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author: sheldon
 * @Date: 3/30/24 9:23 上午
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "游戏详情")
public class LobbyGameInfoVO implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "名称")
    @I18nField
    private String name;

    @Schema(description = "图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;

    @Schema(description = "图标")
    private String iconFileUrl;

    @Schema(description = "游戏图片-正方形")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String seIconI18nCode;

    @Schema(description = "游戏图片-正方形-图标")
    private String seIconI18nCodeFileUrl;

    @Schema(description = "游戏图片-竖版")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String vtIconI18nCode;

    @Schema(description = "游戏图片-竖版-图标")
    private String vtIconI18nCodeFileUrl;

    @Schema(description = "游戏图片-横版")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String htIconI18nCode;

    @Schema(description = "游戏图片-竖版-横版")
    private String htIconI18nCodeFileUrl;

    @Schema(description = "状态:1:开启中,2:维护中,3:已禁用")
    private Integer status;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "描述")
    @I18nField
    private String gameDescI18nCode;

    @Schema(description = "排序")
    private Long sort;

    @Schema(description = "收藏游戏排序")
    private Long collectionSort;

    @Schema(description = "场馆CODE")
    private String venueCode;

    @Schema(description = "游戏CODE")
    private String gameCode;

    @Schema(description = "游戏类型CODE")
    private String gameCategoryCode;

    @Schema(description = "三方参数")
    private JSONObject data;

    @Schema(description = "标签 0:无,1:热门推荐 2:新游戏")
    private Integer label;

    /**
     * 角标 0:News 1:Hot
     */
    @Schema(description = "角标 0:无,1:热门推荐 2:新游戏")
    private Integer cornerLabels;

    @Schema(description = "维护开始时间")
    private Long maintenanceStartTime;

    @Schema(description = "维护结束时间")
    private Long maintenanceEndTime;

    @Schema(description = "是否收藏:true:是,false:否")
    private boolean collect;

    private Long createTime;

    @Schema(description = "一级分类-首页游戏-排序")
    private Long gameOneHomeSort;

    @Schema(description = "首页 - 一级分类-热门排序")
    private Long gameOneHotSort;

    @Schema(description = "二级分类")
    private String gameTwoId;

    @Schema(description = "首页 - 热门排序")
    private Long homeHotSort;

    @Schema(description = "游戏支持币种")
    private String currencyCode;


    @Schema(description = "二级分类排序",hidden = true)
    private Long classTwoSort;

    @Schema(description = "二级分类图片",hidden = true)
    private String classTwoIcon;

    @Schema(description = "二级分类名称CODE",hidden = true)
    @I18nField
    private String gameTwoClassName;

    @Schema(description = "一级分类",hidden = true)
    private String gameOneId;

}
