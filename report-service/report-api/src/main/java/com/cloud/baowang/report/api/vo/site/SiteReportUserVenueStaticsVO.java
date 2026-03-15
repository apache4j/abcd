package com.cloud.baowang.report.api.vo.site;


import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "站点-投注人数 按天统计 VO")
public class SiteReportUserVenueStaticsVO {


    @Schema(description = "日期")
    private String myDay;

    @Schema(description = "日期时间戳")
    private Long dayMillis;

    @Schema(description = "会员统计人数")
    private BigDecimal userCount;

    @Schema(description = "统计注单数")
    private BigDecimal betCount;

    @Schema(description = "统计有效投注")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal validAmount;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "输赢")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal winLoseAmount;

    @Schema(description = "输赢")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal platWinLose;

    @Schema(description = "净输赢")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal platNetWinLose;




}
