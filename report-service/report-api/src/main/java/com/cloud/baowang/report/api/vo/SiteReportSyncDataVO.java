package com.cloud.baowang.report.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "同步站点报表数据")
public class SiteReportSyncDataVO {
    @Schema(description = "开始时间")
    @NotNull(message = "开始时间不能为空")
    private Long startTime;
    @NotNull(message = "结束时间不能为空")
    @Schema(description = "结束时间")
    private Long endTime;
    @Schema(description = "为true表示需要重跑")
    private boolean rerun;
    @Schema(description = "站点code")
    private String siteCode;
}
