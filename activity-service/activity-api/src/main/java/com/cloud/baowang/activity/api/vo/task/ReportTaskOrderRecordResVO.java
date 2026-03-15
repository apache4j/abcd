package com.cloud.baowang.activity.api.vo.task;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * @className: ReportTaskOrderRecordPO
 * @author: wade
 * @description: 任务记录报表
 * @date: 4/11/24 09:14
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "任务报表查询生成数据相应")
public class ReportTaskOrderRecordResVO implements Serializable {

    /**
     * 统计日期（前端显示）
     */
    @Schema(title = "统计日期")
    private String dateTime;
    /**
     * 任务id
     */
    @Schema(title = "统计日期")
    private String taskId;
    /**
     * 任务id
     */
    @Schema(title = "统计日期")
    private List<String> taskIds;



    /**
     * 任务id
     */
    @Schema(title = "任务名称")
    private String taskName;

    /**
     * 站点编码
     */
    @Schema(title = "统计日期")
    private String siteCode;

    /**
     * 任务类型
     */
    @Schema(title = "统计日期")
    private String taskType;

    /**
     * 子任务类型
     */
    @Schema(title = "统计日期")
    private String subTaskType;

    /**
     * 任务发放金额
     */
    @Schema(title = "任务发放金额")
    private BigDecimal sendAmount = BigDecimal.ZERO;

    /**
     * 总计条数，发送人数
     */
    @Schema(title = "总计条数，发送人数")
    private Integer allCount = 0;


    /**
     * 已领取人数
     */
    private Integer receiveCount = 0;

    /**
     * 已领取彩金金额
     */
    @Schema(title = "已领取彩金金额")
    private BigDecimal receiveAmount = BigDecimal.ZERO;

    /**
     * 未领取人数
     */
    @Schema(title = "未领取人数")
    private Integer receiveNoCount = 0;

    /**
     * 未领取人数
     */
    @Schema(title = "统计日期")
    private BigDecimal receiveNoAmount = BigDecimal.ZERO;


    /**
     * 币种
     */
    @Schema(title = "币种")
    private String currencyCode;


    /**
     * 统计日期
     */
    private String staticDate;





}
