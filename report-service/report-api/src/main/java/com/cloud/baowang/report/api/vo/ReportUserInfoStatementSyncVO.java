package com.cloud.baowang.report.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class ReportUserInfoStatementSyncVO {

    @Schema(title = "开始时间戳")
    @NotNull
    private Long startTime;
    @NotNull
    @Schema(title = "结束时间戳")
    private Long endTime;

    @Schema(title = "是否删除开始时间时间戳报表数据")
    private Boolean flag = false;

    @Schema(title = "站点，如果为空，则充跑全部")
    private String siteCode;

}
