package com.cloud.baowang.activity.api.vo.task;

import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @className: SiteTaskConfigPO
 * @author: wade
 * @description: 任务配置
 * @date: 18/9/24 14:38
 */
@Schema(description = "任务保存-存款与邀请-入参")
@Data
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
public class SiteTaskConfigReqVO implements Serializable {

    @Schema(description = "timeZone", hidden = true)
    private String timeZone;

    @Schema(title = "任务id")
    //@NotNull(message = "id have not null")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String id;

    @Schema(title = "站点code", hidden = true)
    private String siteCode;
    /**
     * 任务名称-多语言
     */
    @Schema(title = "任务名称-多语言")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String taskNameI18nCode;


    /**
     * 活动名称-多语言
     */
    @Schema(title = "任务名称-多语言")
    //@NotEmpty(message = "task name have not null")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> taskNameI18nCodeList;

    /**
     * 任务类型
     */
    /*@Schema(title = "任务类型")
    private String taskType;*/

    /**
     * 任务类型
     */
  /*  @Schema(title = "子任务类型")
    private String subTaskType;*/

    /**
     * 子任务类型
     */
    /*@Schema(title = "子任务类型")
    private String subTaskTypeName;*/


    /**
     * 最小配置金额
     */
    @Schema(title = "最小配置金额")
    private BigDecimal minBetAmount;


    /**
     * 彩金奖励
     */
    @Schema(title = "彩金奖励")
    //@NotNull(message = "彩金奖励不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal rewardAmount;

    /**
     * 洗码倍率
     */
    @Schema(title = "洗码倍率")
    //@NotNull(message = "洗码倍率不能为空")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private BigDecimal washRatio;

    /**
     * 场馆类型: 1:体育 2:视讯 3:棋牌 4:电子
     */
    @Schema(title = "场馆类型: 1:体育 2:视讯 3:棋牌 4:电子")
    private Integer venueType;

    /**
     * 场馆类型: 1:体育 2:视讯 3:棋牌 4:电子
     */
    @Schema(title = "场馆类型: 1:体育 2:视讯 3:棋牌 4:电子")
    private String venueTypeName;

    /**
     * 游戏场馆CODE
     */
    @Schema(title = "游戏场馆CODE,多个场馆 code1,code2,code3")
    private List<String> venueCode;

    /**
     * 游戏场馆CODE
     */
    /*@Schema(title = "游戏场馆名称")
    private String venueCodeName;*/

    /**
     * 移动端任务图标
     */
    @Schema(title = "移动端任务图标")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String taskPictureI18nCode;

    @Schema(title = "移动端任务图标-多语言")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> taskPictureI18nCodeList;

    /**
     * PC任务图标
     */
    @Schema(title = "PC任务图标")
    private String taskPicturePcI18nCode;

    @Schema(title = "PC任务图标-多语言")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> taskPicturePcI18nCodeList;

    /**
     * 任务描述,多语言
     * task_description_i18n_code
     */
    @Schema(title = "任务描述,多语言")
    private String taskDescriptionI18nCode;
    @Schema(title = "任务描述-多语言")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> taskDescriptionI18nCodeList;

    /**
     * 任务说明,多语言
     */
    @Schema(title = "任务说明,多语言")
    private String taskDescI18nCode;
    @Schema(title = "任务说明-多语言")
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> taskDescI18nCodeList;


    /**
     * 顺序
     */
    @Schema(title = "顺序")
    private Integer sort;

    /**
     * 状态 0已禁用 1开启中
     */
    @Schema(title = "状态 0已禁用 1开启中")
    private Integer status;

    /**
     * 状态 0已禁用 1开启中
     */
    @Schema(title = "操作人")
    private String operator;

    @Schema(title = "任务配置，每日任务存款配置，每周任务邀请好友人数配置")
    //@NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<TaskSubConfigReqVO> taskConfigJson;


}
