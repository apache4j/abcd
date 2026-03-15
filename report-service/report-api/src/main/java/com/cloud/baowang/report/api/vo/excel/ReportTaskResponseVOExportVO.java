package com.cloud.baowang.report.api.vo.excel;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author 小智
 * @Date 15/5/23 10:34 AM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "任务报表下载excel")
@I18nClass
@ExcelIgnoreUnannotated
public class ReportTaskResponseVOExportVO implements Serializable {
    @ExcelProperty(value = "日期")
    @Schema(title = "统计日期")
    @ColumnWidth(30)
    private String dateTime;


    /**
     * 任务ID
     */
    @ExcelProperty(value = "任务ID")
    @Schema(title = "任务ID")
    private String taskId;

    /**
     * 任务名称
     */
    @ExcelProperty(value = "任务名称")
    @Schema(title = "任务名称")
    @I18nField
    private String taskName;

    /**
     * 子任务类型
     */

    @Schema(title = "任务名称")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SUB_TASK_TYPE)
    private String subTaskType;

    //@ExcelProperty(value = "任务名称")
    @Schema(title = "任务名称")
    private String subTaskTypeText;

    /**
     * 任务类型
     */

    @Schema(title = "任务类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.TASK_TYPE)
    private String taskType;

    @ExcelProperty(value = "任务类型")
    @Schema(title = "任务类型")
    private String taskTypeText;



    /**
     * 发放人数
     */
    @ExcelProperty(value = "发放人数")
    @Schema(title = "发放人数")
    private Integer allCount = 0;

    /**
     * 发放彩金金额
     */
    //@ExcelProperty(value = "发放彩金金额")
    @Schema(title = "任务发放金额")
    private BigDecimal sendAmount = BigDecimal.ZERO;









    /**
     * 发放彩金金额
     */
    @ExcelProperty(value = "发放彩金金额")
    @Schema(title = "发放彩金金额-导出")
    private String sendAmountText;

    public String getSendAmountText() {
        return sendAmount + currencyCode;
    }

    /**
     * 已领取人数
     */
    @ExcelProperty(value = "已领取人数")
    @Schema(title = "已领取人数")
    private Integer receiveCount = 0;

    /**
     * 已领取彩金金额
     */
    //@ExcelProperty(value = "已领取彩金金额")
    @Schema(title = "已领取金额")
    private BigDecimal receiveAmount = BigDecimal.ZERO;

    @ExcelProperty(value = "已领取金额")
    @Schema(title = "已领取金额")
    private String receiveAmountText;

    public String getReceiveAmountText() {
        return receiveAmount + currencyCode;
    }

    /**
     * 未领取人数
     */
    //@ExcelProperty(value = "未领取人数")
    @Schema(title = "未领取人数")
    private Integer receiveNoCount = 0;

    /**
     * 未领取彩金金额
     */
    //@ExcelProperty(value = "未领取彩金金额")
    @Schema(title = "未领取金额")
    private BigDecimal receiveNoAmount = BigDecimal.ZERO;

    //@ExcelProperty(value = "未领取彩金金额")
    @Schema(title = "未领取彩金金额")
    private String receiveNoAmountName;

    public String getReceiveNoAmountName() {
        return receiveNoAmount + currencyCode;
    }



    /**
     * 日期小时维度
     */
    //@ExcelProperty(value = "日期小时维度")
    @Schema(title = "日期小时维度")
    private Long dayHourMillis;



    /**
     * 币种
     */
    //@ExcelProperty(value = "币种")
    @Schema(title = "币种")
    private String currencyCode;


}
