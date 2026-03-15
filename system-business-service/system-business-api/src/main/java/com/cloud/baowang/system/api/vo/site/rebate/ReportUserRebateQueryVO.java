package com.cloud.baowang.system.api.vo.site.rebate;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
public class ReportUserRebateQueryVO extends PageVO {

    @Schema(description = "站点编号",hidden = true)
    private String siteCode;

    @Schema(description ="场馆类型 人工加减额 传-1")
    private String venueType;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description ="开始时间")
    private Long startTime;

    @Schema(description ="结束时间")
    private Long endTime;

    @Schema(description = "当前时区",hidden = true)
    private String timeZone;

    @Schema(description = "当前时区数据库",hidden = true)
    private String timeZoneDb;


}
