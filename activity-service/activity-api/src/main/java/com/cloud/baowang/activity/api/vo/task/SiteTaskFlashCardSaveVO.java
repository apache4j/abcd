package com.cloud.baowang.activity.api.vo.task;

import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteTaskFlashCardSaveVO {




    @Schema(description = "操作人", hidden = true)
    private String operator;

    /**
     * 任务类型
     */
    private String taskType;

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


    @Schema(title = "活动名称-多语言")
    //@NotNull(message = ConstantsCode.PARAM_ERROR)
    private String activityNameI18nCode;

    /**
     * 活动名称-多语言
     */
    @Schema(title = "活动名称-多语言")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> activityNameI18nCodeList;


    /**
     * 活动生效的账户类型
     */
    @Schema(title = "活动生效的账户类型 字典CODE：agent_type")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String accountType;

    /**
     * 活动展示终端
     */
    @Schema(title = "活动展示终端")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String showTerminal;

    @Schema(title = "入口图-移动端")
    //@NotNull(message = ConstantsCode.PARAM_ERROR)
    private String entrancePictureI18nCode;

    /**
     * 入口图-移动端
     */
    @Schema(title = "入口图-移动端")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> entrancePictureI18nCodeList;


    @Schema(title = "入口图-PC端")
    //@NotNull(message = ConstantsCode.PARAM_ERROR)
    private String entrancePicturePcI18nCode;


    /**
     * 入口图-PC端
     */
    @Schema(title = "入口图-PC端")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> entrancePicturePcI18nCodeList;


    /**
     * 状态 0已禁用 1开启中
     */
    @Schema(title = "状态 0已禁用 1开启中")
    private Integer status;


    @Schema(description = "活动简介-多语言")
    //@I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityIntroduceI18nCode;

    @Schema(description = "活动简介-多语言")
    //@NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> activityIntroduceI18nCodeList;


}
