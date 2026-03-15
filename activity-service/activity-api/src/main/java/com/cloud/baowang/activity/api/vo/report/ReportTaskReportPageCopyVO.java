package com.cloud.baowang.activity.api.vo.report;


import com.cloud.baowang.common.core.vo.base.SitePageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;


/**
 * 代理报表 ReqVO
 */
@Data
@Schema(title = "任务报表请求 ReqVO")
public class ReportTaskReportPageCopyVO extends SitePageVO {


    @Schema(title = "统计日期-开始时间")
    private Long startTime;
    @Schema(title = "统计日期-结束时间")
    private Long endTme;

    @Schema(title = "任务名称 system_param(sub_task_type)")
    private String taskName;
    @Schema(title = "任务ID")
    private List<String> taskIds;

    @Schema(title = "任务ID")
    private String taskId;
    @Schema(title = "任务类型 system-param(task_type)")
    private String taskType;
    @Schema(title = "时区", hidden = true)
    private String timezone;

    @Schema(title = "时区数据库查询", hidden = true)
    private String timezoneDb;


}
