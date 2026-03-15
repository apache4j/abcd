package com.cloud.baowang.activity.api.vo.task;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(title = "任务客户端详情返回")
@I18nClass
public class APPTaskResponseVO implements Serializable {

    @Schema(title = "累计奖励")
    private BigDecimal totalAmount;

    /**
     * 是返回未完成的还是所有的
     */
    @Schema(title = "每周任务列表")
    @I18nField
    private List<TaskUserConfigAppResVO> weeklyTask;

    /**
     * 是返回未完成的还是所有的
     */
    @Schema(title = "每日任务列表")
    @I18nField
    private List<TaskUserConfigAppResVO> dailyTask;




    @Schema(title = "每日任务截止时间戳")
    private Long dailyTaskEndTime;

    /**
     * 是返回未完成的还是所有的
     */
    @Schema(title = "新人任务表")
    @I18nField
    private List<TaskUserConfigAppResVO> noviceTask;

    @Schema(title = "每周任务截止时间戳")
    private Long weeklyEndTime;

    @Schema(title = "新人任务截止时间戳")
    private Long noviceEndTime;


    @Schema(title = "平台币")
    private String currency;
    @Schema(title = "平台币名称")
    private String currencyName;

    @Schema(title = "平台币符号")
    private String platCurrencySymbol;


    /*@Schema(title = "任务阶梯配置，每日任务存款配置，每周任务邀请好友人数配置")
    private List<TaskSubConfigReqVO> taskConfigJson;*/

    /**
     * 状态 任务配置1展开，2-隐藏，默认是1
     */
    @Schema(title = "任务配置1展开，2-隐藏，默认是1")
    private Integer expandStatus;

    /**
     * 判断任务是否都是禁止，只有一条任务没有禁止就是true，否则是false
     */
    @Schema(title = "判断任务是否都是禁止，只要有一条任务没有禁止就是true，否则是false")
    private Boolean allTaskStatus;

    @Schema(title = "每周任务是否开启卡图配置,true是开启,false是关闭")
    private Boolean weekFlashCardTaskFlag = false;

    @Schema(title = "每日任务是否开启卡图配置,true是开启,false是关闭")
    private Boolean dailyTFlashCardTaskFlag = false;

    @Schema(title = "周任务卡图配置")
    private SiteTaskFlashCardBaseAPPRespVO weeklyTaskFlashFlag;

    @Schema(title = "日任务卡图配置")
    private SiteTaskFlashCardBaseAPPRespVO dailyTaskFlashFlag;


}
