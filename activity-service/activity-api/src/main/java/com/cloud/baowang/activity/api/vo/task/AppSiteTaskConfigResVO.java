package com.cloud.baowang.activity.api.vo.task;

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
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

/**
 * @className: SiteTaskConfigPO
 * @author: wade
 * @description: 任务配置
 * @date: 18/9/24 14:38
 */
@Schema(description = "客户端任务详情")
@Data
@Builder
@Slf4j
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
public class AppSiteTaskConfigResVO {
    @Schema(title = "任务id")
    private String id;
    /**
     * 任务名称-多语言
     */
    @Schema(title = "任务名称-多语言")
    @I18nField
    private String taskNameI18nCode;



    /**
     * 任务类型
     */
    @Schema(title = "任务类型")
    private String taskType;

    /**
     * 任务类型
     */
    @Schema(title = "子任务类型")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.ENABLE_DISABLE_TYPE)
    private String subTaskType;

    /**
     * 子任务类型
     */
    @Schema(title = "子任务类型")
    private String subTaskTypeText;

    /**
     * 最小配置金额
     */
    @Schema(title = "最小配置金额")
    private BigDecimal minBetAmount;

    /**
     * 彩金奖励
     */
    @Schema(title = "彩金奖励")
    private BigDecimal rewardAmount;

    /**
     * 洗码倍率
     */
    @Schema(title = "洗码倍率")
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
    @Schema(title = "游戏场馆CODE")
    private String venueCode;

    /**
     * 游戏场馆CODE
     */
    @Schema(title = "游戏场馆名称")
    private String venueCodeName;

    /**
     * 移动端任务图标
     */
    @Schema(title = "移动端任务图标")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String taskPictureI18nCode;

    @Schema(title = "移动端任务图标-多语言")
    private List<I18nMsgFrontVO> taskPictureI18nCodeList;

    /**
     * PC任务图标
     */
    @Schema(title = "PC任务图标")
    private String taskPicturePcI18nCode;

    @Schema(title = "PC任务图标-多语言")
    private List<I18nMsgFrontVO> taskPicturePcI18nCodeList;

    /**
     * 任务说明,多语言
     */
    @Schema(title = "任务说明,多语言")
    private String taskDescI18nCode;
    @Schema(title = "任务说明-多语言")
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
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_TYPE)
    private Integer status;

    @Schema(description = "状态 0已禁用 1开启中")
    private String statusText;



}
