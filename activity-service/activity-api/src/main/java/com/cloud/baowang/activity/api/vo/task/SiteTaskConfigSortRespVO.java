package com.cloud.baowang.activity.api.vo.task;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 任务信息
 */
@Data
@Schema(title = "任务信息-返回活动排序列表")
@I18nClass
public class SiteTaskConfigSortRespVO {

    /**
     * id
     */
    @Schema(title = "主键id")
    private String id;
    /**
     * 站点code
     */
    @Schema(title = "站点code", hidden = true)
    private String siteCode;

    @Schema(title = "任务名称")
    @I18nField
    private String taskNameI18nCode;


    /**
     * 活动分类-活动分类主键
     */
    @Schema(title = "任务分类")
    private String taskType;


    /**
     * 顺序
     */
    @Schema(title = "顺序")
    private Integer sort;


    /**
     * 状态 0:禁用 1:启用
     */
    @Schema(description = "状态 0:禁用 1:启用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_TYPE)
    private Integer status;


    @Schema(description = "状态多语言")
    private String statusText;


}