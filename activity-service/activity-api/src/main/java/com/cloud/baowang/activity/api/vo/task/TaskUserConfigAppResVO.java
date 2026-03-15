package com.cloud.baowang.activity.api.vo.task;

import com.cloud.baowang.activity.api.enums.task.TaskReceiveStatusEnum;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalTwoDecimalSerializer;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Digits;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author: wade
 */
@Data
@Schema(title = "任务客户端 任务配置详情")
@I18nClass
public class TaskUserConfigAppResVO implements Serializable {

    @Schema(description = "任务配置Id")
    private String id;

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
    @Schema(description = "最小配置金额/盈利/负盈利,每日存款-最大存款配置")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER) // 保持数字类型
    @Digits(integer = 10, fraction = 2) // 限制最多2位小数
    private BigDecimal minBetAmount;

    /**
     * 每周邀请任务-最大邀请人数配置
     */
    @Schema(description = "每周邀请任务-最大邀请人数配置")
    private Integer minBetCount;

    /**
     * 最小配置金额
     */
    @Schema(description = "最小配置金额/盈利/负盈利")
    private String minBetAmountText;

    public String getMinBetAmountText() {
        if (minBetAmount == null) {
            return null;
        }
        return minBetAmount.stripTrailingZeros().toPlainString();
    }

    /**
     *
     */
    @Schema(description = "会员在当周或者当日达到的投注金额/盈利/负盈利 每日存款任务-个人实际存款")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER) // 保持数字类型
    @Digits(integer = 10, fraction = 2) // 限制最多2位小数
    private BigDecimal achieveAmount;

    @Schema(description = "邀请任务-个人实际邀请")
    private Integer achieveCount;

    public BigDecimal getAchieveAmount() {
        return achieveAmount == null ? BigDecimal.ZERO : achieveAmount.setScale(2, RoundingMode.DOWN);
    }

    /**
     * 彩金奖励
     */
    @Schema(description = "彩金奖励, 存款活动-阶梯累计奖励金额之和")
    @JsonFormat(shape = JsonFormat.Shape.NUMBER) // 保持 JSON 输出为数字
    @JsonSerialize(using = BigDecimalTwoDecimalSerializer.class) // 自定义序列化
    @Digits(integer = 10, fraction = 2) // 限制最多2位小数
    private BigDecimal rewardAmount;


    /**
     * 移动端任务图标
     */
    @I18nField(type = I18nFieldTypeConstants.FILE)
    @Schema(description = "移动端任务图标")
    private String taskPictureI18nCode;

    /**
     * 移动端任务图标
     */

    @Schema(description = "移动端任务图标，显示决定路径，客户端显示使用")
    private String taskPictureI18nCodeFileUrl;

    /**
     * PC任务图标
     */
    @I18nField(type = I18nFieldTypeConstants.FILE)
    @Schema(description = "PC任务图标")
    private String taskPicturePcI18nCode;

    @Schema(description = "PC任务图标,显示决定路径，客户端显示使用")
    private String taskPicturePcI18nCodeFileUrl;

    /**
     * 任务说明,多语言
     */
    @I18nField
    @Schema(description = "任务说明")
    private String taskDescI18nCode;
    /**
     * 任务描述,多语言
     */
    @I18nField
    @Schema(description = "任务描述")
    private String taskDescriptionI18nCode;

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
    @Schema(description = "状态 3- 未完成，0-已完成 1-已领取 2-已经过期 ")
    private Integer taskStatus;

    /**
     * 子任务阶梯总状态 表示任务集合的状态：含义所有子阶梯状态都是已经领取，总的任务状态才是已完成，其他子阶梯任务一个的都是另外一个状态
     */
    @Schema(description = "0-未完成 1-已完成,用于每日任务/每周任务 子任务阶梯总状态 表示任务集合的状态：含义所有子阶梯状态都是已经领取，总的任务状态才是已完成，其他子阶梯任务一个的都是另外一个状态 ")
    private Integer totalTaskStatus = 1;

    /**
     * 状态 0 未完成，已完成 已经领取 没有过期，过期了就是下一次未完成，过期就失效了
     */
    @Schema(description = "状态 3未完成，0已完成 1 已领取 2已经过期 ")
    private String taskStatusText;

    @Schema(description = "过期时间,单位秒 ")
    private Long expireTime;

    @Schema(description = "货币")
    private String currency;


    @Schema(title = "平台币名称")
    private String currencyName;

    @Schema(title = "平台币符号")
    private String platCurrencySymbol;

    @Schema(title = "任务阶梯配置，每日任务存款配置，每周任务邀请好友人数配置-每日任务/每周任务配置奖励阶梯以及获取情况")
    private List<APPTaskSubConfigReqVO> taskConfigJson;


}