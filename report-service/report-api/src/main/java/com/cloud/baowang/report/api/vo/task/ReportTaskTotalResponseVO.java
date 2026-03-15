package com.cloud.baowang.report.api.vo.task;


import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 代理报表 resp
 */
@Data
@Schema(title = "任务报表每页/全部合计 resp")
public class ReportTaskTotalResponseVO implements Serializable {

    @Schema(title = "统计日期")
    private String dateTime;

    /**
     * 任务发放金额
     */
    @Schema(title = "任务发放金额任务发放金额")
    private BigDecimal sendAmount = BigDecimal.ZERO;

    /**
     * 任务发放金额
     */
    @Schema(title = "任务发放人数")
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
