package com.cloud.baowang.activity.api.vo;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 活动基础信息的所有字段属性
 */
@Data
@Schema(title = "活动基础信息-返回活动排序列表")
@I18nClass
public class ActivityBaseSortRespVO {

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

    @Schema(title = "活动名称")
    @I18nField
    private String activityNameI18nCode;



    /**
     * 活动分类-活动分类主键
     */
    @Schema(title = "活动分类-活动分类主键")
    private String labelId;


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