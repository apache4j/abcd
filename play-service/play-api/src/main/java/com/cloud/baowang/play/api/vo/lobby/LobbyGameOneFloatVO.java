package com.cloud.baowang.play.api.vo.lobby;

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
 * @Date: 3/29/24 5:29 下午
 */
@Data
@Builder
@I18nClass
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "游戏侧边栏一级分类悬浮对象")
public class LobbyGameOneFloatVO implements Serializable {

    @Schema(title = "二级分类ID",hidden = true)
    private String gameTwoId;

    @I18nField
    @Schema(title = "悬浮名称")
    private String floatNameI18nCode;


    @Schema(title = "品牌图标",hidden = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String logoIconI18nCode;


    @Schema(title = "品牌图标地址")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String logoIconI18nCodeFileUrl;

    @Schema(title = "中图标",hidden = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String mediumIconI18nCode;


    @Schema(title = "中图标地址")
    private String mediumIconI18nCodeFileUrl;

    @Schema(title = "排序")
    private Integer sort;

    @Schema(title = "场馆排序",hidden = true)
    private Integer venueSort;

    @Schema(title = "二级分类排序",hidden = true)
    private Integer twoSort;

    @Schema(title = "一级分类",hidden = true)
    private String gameOneId;

    @Schema(description = "状态:1:开启中,2:维护中,3:已禁用")
    private Integer status;

    @Schema(description = "维护开始时间")
    private Long maintenanceStartTime;

    @Schema(description = "维护结束时间")
    private Long maintenanceEndTime;

    @Schema(title = "场馆",hidden = true)
    private String venueCode;

    @Schema(title = "场馆类型",hidden = true)
    private String model;


    @Schema(description = "冠名标签")
    private Integer siteLabelChangeType;



}
