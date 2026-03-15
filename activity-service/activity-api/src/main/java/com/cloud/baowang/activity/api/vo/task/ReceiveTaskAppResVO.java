package com.cloud.baowang.activity.api.vo.task;

import com.cloud.baowang.activity.api.enums.task.TaskReceiveStatusEnum;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: wade
 */
@Data
@Schema(title = "任务客户端 领取奖励结果")
public class ReceiveTaskAppResVO implements Serializable {

    /**
     * 任务名称-多语言
     */
    @Schema(description = "任务名称")
    @I18nField
    private String taskNameI18nCode;

    /**
     * 任务类型
     */
    @Schema(description = "任务类型")
    private String taskType;

    /**
     * 子任务类型
     */
    @Schema(description = "子任务类型")
    private String subTaskType;

    /**
     * 最小配置金额
     */
    @Schema(description = "最小配置金额/盈利/负盈利")
    private BigDecimal minBetAmount;

    /**
     *
     */
    @Schema(description = "会员在当周或者当日达到的投注金额/盈利/负盈利")
    private BigDecimal achieveAmount;

    /**
     * 彩金奖励
     */
    @Schema(description = "彩金奖励")
    private BigDecimal rewardAmount;


    /**
     * 移动端任务图标
     */
    @I18nField
    @Schema(description = "移动端任务图标")
    private String taskPictureI18nCode;

    /**
     * PC任务图标
     */
    @I18nField
    @Schema(description = "PC任务图标")
    private String taskPicturePcI18nCode;

    /**
     * 任务说明,多语言
     */
    @I18nField
    @Schema(description = "任务说明")
    private String taskDescI18nCode;

    /**
     * 顺序
     */
    private Integer sort;

    /**
     * 状态 0已禁用 1开启中
     */
    private Integer status;


    /**
     * 状态 0 未完成，已完成 已经领取 没有过期，过期了就是下一次未完成，过期就失效了
     * {@link  TaskReceiveStatusEnum}
     */
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.TASK_RECEIVE_STATUS)
    @Schema(description = "状态 0 未完成，1已完成 2 已领取 3已经过期 ")
    private Integer taskStatus;

    /**
     * 状态 0 未完成，已完成 已经领取 没有过期，过期了就是下一次未完成，过期就失效了
     */
    @Schema(description = "状态 0 未完成，1已完成 2 已领取 3已经过期 ")
    private String taskStatusText;




}
