package com.cloud.baowang.report.api.vo.task;


import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;


/**
 * 代理报表 ReqVO
 */
@Data
@Schema(title = "任务报表请求 ReqVO")
public class ReportTaskReportPageVO extends SitePageVO {


    @Schema(title = "统计日期-开始时间")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Long startTime;
    @Schema(title = "统计日期-结束时间")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private Long endTme;

    @Schema(title = "任务名称 system_param(sub_task_type)")
    private String taskName;
    @Schema(title = "任务ID")
    private String taskId;
    @Schema(title = "任务类型 system-param(task_type)")
    private String taskType;
    @Schema(title = "时区", hidden = true)
    private String timezone;

    @Schema(title = "时区数据库查询", hidden = true)
    private String timezoneDb;


    @Schema(title = "是否下载", hidden = true)
    private Boolean downLoad = Boolean.FALSE;

}
