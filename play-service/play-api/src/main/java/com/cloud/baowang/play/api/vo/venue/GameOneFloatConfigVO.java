package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author qiqi
 */
@Schema(description = "游戏平台VO对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@I18nClass
public class GameOneFloatConfigVO implements Serializable {

    private String id;

    @Schema(description = "一级分类")
    private String gameOneId;

    @Schema(description = "二级分类")
    private String gameTwoId;

    @Schema(description = "场馆")
    private String venueCode;

    @Schema(description = "一级分类名称")
    @I18nField
    private String gameOneName;

    @Schema(description = "悬浮名称-多语言CODE")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String floatNameI18nCode;

    @Schema(description = "悬浮名称")
    @I18nField
    private String floatNameI18nCodeName;

    @Schema(description = "悬浮名称-多语言")
    private List<I18nMsgFrontVO> floatNameI18nCodeList;

    @Schema(description = "品牌图标-多语言CODE")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String logoIconI18nCode;

    @Schema(description = "品牌图标-多语言")
    private List<I18nMsgFrontVO> logoIconI18nCodeList;

    @Schema(description = "品牌图标地址")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String logoIconI18nCodeUrl;

    @Schema(description = "品牌图标地址")
    private String logoIconI18nCodeUrlFileUrl;


    @Schema(description = "中图标-多语言CODE")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String mediumIconI18nCode;

    @Schema(description = "中图标-多语言")
    private List<I18nMsgFrontVO> mediumIconI18nCodeList;

    @Schema(description = "中图标")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String mediumIconI18nCodeUrl;

    @Schema(description = "中图标")
    @I18nField
    private String mediumIconI18nCodeUrlFileUrl;

    @Schema(description = "状态 1=开启中,3=已禁用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_CLASS_STATUS_TYPE)
    private Integer status;

    @Schema(description = "状态名称")
    private String statusText;

    @Schema(description = "一级分类模板")
    private String model;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建时间")
    private Long createdTime;

    @Schema(description = "更新人")
    private String updater;

    @Schema(description = "更新时间")
    private Long updatedTime;


}
