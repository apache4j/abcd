package com.cloud.baowang.activity.api.vo.report;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/3 11:51
 * @Version: V1.0
 **/
@Data
@Schema(description = "活动数据报表请求参数")
public class DataReportReqVO extends PageVO {
    @Schema(description = "站点编号",hidden = true)
    private String siteCode;

    @Schema(description = "开始时间 时间戳 秒")
    private Long beginTime;
    @Schema(description = "结束时间 时间戳 秒")
    private Long endTime;

    @Schema(description = "活动名称")
    private String activityName;

    @Schema(description = "活动编号")
    private String activityNo;

    @Schema(description = "当前时区",hidden = true)
    private String timeZone;

    @Schema(description = "当前时区数据库",hidden = true)
    private String timeZoneDb;

    @Schema(description = "活动模板")
    private String activityTemplate;

}
