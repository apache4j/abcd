package com.cloud.baowang.activity.api.vo.task;

import com.cloud.baowang.activity.api.enums.ActivityEligibilityEnum;
import com.cloud.baowang.activity.api.vo.*;
import com.cloud.baowang.activity.api.vo.redbag.RedBagRainRespVO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.enums.EnableStatusEnum;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import com.google.common.collect.Lists;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
public class SiteTaskFlashCardBaseRespVO {

    @Schema(description = "id")
    private String id;
    /**
     * 站点code
     */
    @Schema(title = "站点code", hidden = true)
    private String siteCode;





    @Schema(title = "活动名称")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityNameI18nCode;

    /**
     * 活动名称-多语言
     */
    @Schema(title = "活动名称-多语言")
    private List<I18nMsgFrontVO> activityNameI18nCodeList;


    /**
     * 活动生效的账户类型
     */
    @Schema(title = "活动生效的账户类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE)
    private String accountType;

    @Schema(description = "活动生效的账户类型")
    private String accountTypeText;


    /**
     * 活动展示终端
     */
    @Schema(title = "活动展示终端")
    @I18nField(type = I18nFieldTypeConstants.DICT_CODE_TO_STR, value = CommonConstant.DEVICE_TERMINAL)
    private String showTerminal;

    @Schema(title = "活动展示终端名称")
    private String showTerminalText;






    /**
     * 状态 0已禁用 1开启中
     */
    @Schema(title = "状态 0已禁用 1开启中")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;

    /**
     * 状态 0已禁用 1开启中
     */
    @Schema(title = "状态 0已禁用 1开启中")
    private String statusText;

    @Schema(title = "入口图-移动端")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String entrancePictureI18nCode;

    /**
     * 入口图-移动端
     */
    @Schema(description = "入口图-移动端")
    private List<I18nMsgFrontVO> entrancePictureI18nCodeList;
    /**
     * 入口图-PC端
     */
    @Schema(title = "入口图-PC端")
    @I18nField(type = I18nFieldTypeConstants.FILE_LIST)
    private String entrancePicturePcI18nCode;

    /**
     * 入口图-PC端
     */
    @Schema(description = "入口图-PC端")
    private List<I18nMsgFrontVO> entrancePicturePcI18nCodeList;





    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

    /**
     * 创建人
     */
    @Schema(description = "创建时间")
    private Long createdTime;
    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updater;

    /**
     * 修改时间
     */
    @Schema(description = "修改时间")
    private Long updatedTime;


    /**
     * 活动参与终端
     */
    private String supportTerminal;




    /**
     * 任务类型
     */
    @Schema(title = "任务类型")
    private String taskType;




    /**
     * 活动简介,多语言
     */

    @Schema(description = "活动简介-多语言")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityIntroduceI18nCode;

    @Schema(description = "活动简介-多语言")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> activityIntroduceI18nCodeList = new ArrayList<>();




}
