package com.cloud.baowang.report.api.vo.user.complex;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "综合数据报表查询VO")
public class AdminIntegrateDataReportReqVO extends PageVO {
    @Schema(description = "开始时间")
    private Long beginTime;
    @Schema(description = "结束时间")
    private Long endTime;
    @Schema(description = "站点编码")
    private String siteCode;
    @Schema(description = "币种")
    private String currencyCode;

    @Schema(title = "转换为平台币 true 是 false 否")
    private Boolean toPlatCurr;

    @Schema(title = "是否导出 true 是 false 否")
    private Boolean exportFlag;

    @Schema(description = "当前时区",hidden = true)
    private String timeZone;

    @Schema(description = "当前时区数据库",hidden = true)
    private String timeZoneDb;

}
