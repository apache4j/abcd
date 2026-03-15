package com.cloud.baowang.activity.api.vo.task;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
public class SiteTaskFlashCardBaseAPPRespVO {







    @Schema(title = "活动名称")
    @I18nField
    private String activityNameI18nCode;



    /**
     * 活动生效的账户类型
     *//*
    @Schema(title = "活动生效的账户类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private Integer accountType;

    @Schema(description = "活动生效的账户类型")
    private String accountTypeText;


    *//**
     * 活动展示终端
     *//*
    @Schema(title = "活动展示终端")
    @I18nField(type = I18nFieldTypeConstants.DICT_CODE_TO_STR, value = CommonConstant.DEVICE_TERMINAL)
    private String showTerminal;

    @Schema(title = "活动展示终端名称")
    private String showTerminalText;
*/



    /**
     * 状态 0已禁用 1开启中
     */
    /*@Schema(title = "状态 0已禁用 1开启中")
    private Integer status;*/

    @Schema(title = "入口图-移动端")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String entrancePictureI18nCode;

    @Schema(title = "入口图-移动端")
    private String entrancePictureI18nCodeFileUrl;

    /**
     * 入口图-PC端
     */
    @Schema(title = "入口图-PC端")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String entrancePicturePcI18nCode;

    @Schema(title = "入口图-PC端")
    private String entrancePicturePcI18nCodeFileUrl;

    /**
     * 任务类型
     */
    /*@Schema(title = "任务类型-week-daily")
    private String taskType;*/



    /**
     * 活动简介,多语言
     */

    @Schema(description = "活动简介-多语言")
    @I18nField()
    private String activityIntroduceI18nCode;





}
