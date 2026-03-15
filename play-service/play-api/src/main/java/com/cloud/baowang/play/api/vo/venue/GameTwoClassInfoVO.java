package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author qiqi
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "游戏平台VO对象")
@I18nClass
public class GameTwoClassInfoVO implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "分类名称")
    private String typeName;

    @Schema(description = "一级分类ID")
    private String gameOneId;

    @Schema(description = "一级分类名称")
    private String gameOneName;

    @Schema(description = "状态 code:platform_class_status_type")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.PLATFORM_CLASS_STATUS_TYPE)
    private Integer status;

    @Schema(description = "状态名称")
    private String statusText;

    @Schema(description = "模板名称")
    private String modelName;

    @Schema(description = "模板CODE")
    private String modelCode;

    @Schema(description = "图片CODE", required = true)
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;

    @Schema(description = "图片CODE")
    private String iconFileUrl;


    @Schema(description = "分类名称-多语言CODE", required = true)
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String typeI18nCode;

    @Schema(description = "分类名称-多语言")
    private List<I18nMsgFrontVO> typeI18nCodeList;

    @Schema(description = "游戏详情")
    private List<TwoGameInfoVO> gameInfoList;

    @Schema(description = "游戏条数,点击查询:游戏管理页面分页接口")
    private Integer gameSize;

    @Schema(description = "创建人")
    private String creator;

    @Schema(description = "创建时间")
    private Long createdTime;

    @Schema(description = "更新人")
    private String updater;

    @Schema(description = "更新时间")
    private Long updatedTime;

    @Schema(description = "二级分类游戏横版图标")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String htIconI18nCode;

    @Schema(description = "皮肤4:二级分类游戏横版图标-多语言")
    private List<I18nMsgFrontVO> htIconI18nCodeList;

    /**
     * 排序
     */
    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "冠名标签")
    private Integer siteLabelChangeType;


}
