package com.cloud.baowang.report.api.vo.task;


import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@I18nClass
@Schema(title = "任务报表 返回Response")
public class ReportTaskResponseVO {


    @Schema(title = "统计日期-开始与结束时间")
    private String dateTime;

    /**
     * 统计日期
     */
    @Schema(title = "统计日期")
    private String staticDate;


    /**
     * 站点日期 当天起始时间戳
     */
    @Schema(title = "当天起始时间戳")
    private Long dayMillis;

    /**
     * 日期小时维度
     */
    @Schema(title = "日期小时维度")
    private Long dayHourMillis;

    /**
     * 任务id
     */
    @Schema(title = "任务id")
    private String taskId;


    /**
     * 任务类型
     */
    @Schema(title = "任务类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.TASK_TYPE)
    private String taskType;

    /**
     * 任务类型
     */
    @Schema(title = "任务类型")
    private String taskTypeText;

    /**
     * 子任务类型
     */
    @Schema(title = "子任务类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SUB_TASK_TYPE)
    private String subTaskType;

    /**
     * 子任务类型
     */
    @Schema(title = "子任务类型")
    private String subTaskTypeText;



    /**
     * 子任务类型
     */
    @I18nField
    @Schema(title = "任务名称")
    private String taskName;

    /**
     * 任务发放金额
     */
    @Schema(title = "任务发放金额")
    private BigDecimal sendAmount = BigDecimal.ZERO;

    /**
     * 发放人数
     */
    @Schema(title = "发放人数")
    private Integer allCount = 0;

    /**
     * 已领取人数
     */
    @Schema(title = "已领取人数")
    private Integer receiveCount = 0;

    /**
     * 已领取金额
     */
    @Schema(title = "已领取金额")
    private BigDecimal receiveAmount = BigDecimal.ZERO;

    /**
     * 未领取人数
     */
    @Schema(title = "未领取人数")
    private Integer receiveNoCount = 0;

    /**
     * 未领取金额
     */
    @Schema(title = "未领取金额")
    private BigDecimal receiveNoAmount = BigDecimal.ZERO;

    /**
     * 币种
     */
    @Schema(title = "币种")
    private String currencyCode;


}
