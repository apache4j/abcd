package com.cloud.baowang.report.po;

import com.baomidou.mybatisplus.annotation.TableName;
import com.cloud.baowang.common.mybatis.base.SiteBasePO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @className: ReportTaskOrderRecordPO
 * @author: wade
 * @description: 任务记录报表
 * @date: 4/11/24 09:14
 */
@Data
@Accessors(chain = true)
@TableName("report_task_order_record")
public class ReportTaskOrderRecordPO extends SiteBasePO {

    /**
     * 站点日期 当天起始时间戳
     */
    private Long dayMillis;

    /**
     * 日期小时维度
     */
    private Long dayHourMillis;

    /**
     * 任务id
     */
    private Long taskId;


    /**
     * 任务类型
     */
    private String taskType;

    /**
     * 子任务类型
     */
    private String subTaskType;

    /**
     * 任务发放金额
     */
    private BigDecimal sendAmount;

    /**
     * 已领取人数
     */
    private Integer receiveCount;

    /**
     * 已领取金额
     */
    private BigDecimal receiveAmount = BigDecimal.ZERO;

    /**
     * 未领取人数
     */
    private Integer receiveNoCount ;

    /**
     * 未领取金额
     */
    private BigDecimal receiveNoAmount = BigDecimal.ZERO;

    /**
     * 币种
     */
    private String currencyCode;


}
