package com.cloud.baowang.report.api.vo.agent;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/11/5 11:03
 * @Version: V1.0
 **/
@Data
@Schema(description = "商务总代报表数据初始化条件")
public class ReportTopAgentStaticsCondVO {
    @Schema(description = "站点编号")
    private String siteCode;
    @Schema(title =   "统计日期 dayMillis")
    private Long startDayMillis;
    @Schema(title =   "统计日期 dayMillis")
    private Long endDayMillis;
    @Schema(description = "报表统计类型 0:日报 1:月报")
    private String reportType;
    private String timeZone;
}
