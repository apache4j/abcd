package com.cloud.baowang.report.api.vo.rechagerwithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportUserDepositWithdrawDayReqParam {


    @Schema(description = "开始时间")
    private Long startTime;

    @NotNull(message = "结束时间不能为空")
    private Long endTime;

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "时区")
    private String timeZone;
}
