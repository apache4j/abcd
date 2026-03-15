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
import java.math.BigDecimal;

/**
 * @Author: sheldon
 * @Date: 3/30/24 9:23 上午
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
@Schema(title = "游戏大厅-单场馆类型数据详情")
public class LobbySignVenueInfoVO implements Serializable {

    @Schema(title = "游戏一级分类ID")
    private String gameOneClassId;


    @Schema(description = "名称")
    @I18nField
    private String name;

    @Schema(description = "图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;

    @Schema(description = "图标")
    private String iconFileUrl;

    @Schema(description = "场馆CODE")
    private String venueCode;

    @Schema(description = "维护开始时间")
    private Long maintenanceStartTime;

    @Schema(description = "维护结束时间")
    private Long maintenanceEndTime;

    @Schema(description = "状态:1:开启中,2:维护中,3:已禁用")
    private Integer status;

    @Schema(description = "排序")
    private Long sort;

    @Schema(description = "备注")
    private String remark;

    @Schema(title = "游戏横版图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String htIconI18nCode;

    @Schema(description = "游戏横版图标-图标")
    private String htIconI18nCodeFileUrl;

    @Schema(title = "小图标6")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String smallIcon6I18nCode;

    @Schema(description = "小图标6-图标")
    private String smallIcon6I18nCodeFileUrl;


    @Schema(title = "小图标5")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String smallIcon5I18nCode;

    @Schema(description = "小图标5-图标")
    private String smallIcon5I18nCodeFileUrl;


    @Schema(title = "小图标4")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String smallIcon4I18nCode;

    @Schema(description = "小图标4-图标")
    private String smallIcon4I18nCodeFileUrl;


    @Schema(title = "小图标3")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String smallIcon3I18nCode;

    @Schema(description = "小图标3-图标")
    private String smallIcon3I18nCodeFileUrl;


    @Schema(title = "小图标2")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String smallIcon2I18nCode;

    @Schema(description = "小图标2-图标")
    private String smallIcon2I18nCodeFileUrl;


    @Schema(title = "小图标1")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String smallIcon1I18nCode;

    @Schema(description = "小图标1-图标")
    private String smallIcon1I18nCodeFileUrl;

    @Schema(description = "背景图-多语言", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String pcBackgroundCode;

    @Schema(description = "PC背景图-图标")
    private String pcBackgroundCodeFileUrl;


    @Schema(title = "描述")
    @I18nField
    private String venueDescI18nCode;


    @Schema(description = "场馆图标-多语言", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String pcLogoCode;

    @Schema(description = "场馆PC品牌图标")
    private String pcLogoCodeFileUrl;


    @Schema(description = "场馆PC图标", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String pcIcon;

    @Schema(description = "场馆PC图标")
    private String pcIconFileUrl;

    @Schema(description = "皮肤4:中等图")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String middleIconI18nCode;

    @Schema(description = "皮肤4:中等图")
    private String middleIconI18nCodeFileUrl;


    @Schema(description = "场馆H5图标", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String h5Icon;

    @Schema(description = "场馆H5图标")
    private String h5IconFileUrl;

    @Schema(description = "最高返水")
    private BigDecimal maxRebateAmount;


    @Schema(description = "场馆描述-多语言")
    @I18nField
    private String venueDesc;


    @Schema(description = "赛事数量")
    private Long eventsNumber;


    @Schema(description = "冠名标签")
    private Integer siteLabelChangeType;


}
