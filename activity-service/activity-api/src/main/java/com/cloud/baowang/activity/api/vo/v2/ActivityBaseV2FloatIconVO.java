package com.cloud.baowang.activity.api.vo.v2;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(title = "浮标")
@I18nClass
public class ActivityBaseV2FloatIconVO implements Serializable {

    @Schema(title = "主键id")
    private String id;

    @Schema(title = "活动编号")
    private String activityNo;

    @Schema(title = "站点code", hidden = true)
    private String siteCode;

    @Schema(title = "活动名称")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityNameI18nCode;

    @Schema(title = "活动名称-多语言")
    private List<I18nMsgFrontVO> activityNameI18nCodeList;

    @Schema(title = "活动模板-同system_param activity_template")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_TEMPLATE_V2)
    private String activityTemplate;

    @Schema(title = "活动模板-同system_param activity_template")
    private String activityTemplateText;

    @Schema(title = "未登录首页浮动图标(移动端)-code")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String floatIconAppI18nCode;

    @Schema(title = "未登录首页浮动图标(移动端)-list")
    private String floatIconAppI18nCodeFileUrl;

    @Schema(title = "未登录首页浮动图标(PC端)-code")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String floatIconPcI18nCode;

    @Schema(title = "未登录首页浮动图标(PC端)-list")
    private String floatIconPcI18nCodeFileUrl;

    @Schema(title = "浮标排序 越大越靠前")
    private Integer floatIconSort;

    @Schema(description = "状态 0:禁用 1:启用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_TYPE)
    private Integer status;

    @Schema(description = "状态多语言")
    private String statusText;

    @Schema(description = "h5活动跳转URl")
    private String h5ActivityUrl;

}
