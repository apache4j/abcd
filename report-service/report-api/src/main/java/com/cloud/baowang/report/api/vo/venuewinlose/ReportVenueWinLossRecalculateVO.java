package com.cloud.baowang.report.api.vo.venuewinlose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "场馆盈亏重算")
public class ReportVenueWinLossRecalculateVO {
    @Schema(description = "站点code")
    private String siteCode;
    @Schema(description = "开始时间")
    private Long startTime;
    @Schema(description = "结束时间")
    private Long endTime;
}
