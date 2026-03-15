package com.cloud.baowang.user.api.vo.site;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: wade
 */
@Data
@Schema(description = "数据对比 曲线图 VO")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SiteDataCompareGraphVO {

    @Schema(title = "1-日，2-月，3-年")
    private Integer type = 1;

    @Schema(title = "横坐标 每时/每天/每月")
    private List<String> dayList;

    @Schema(title = "本日数据/本月数据/本年数据")
    private List<BigDecimal> currentData;

    @Schema(title = "对比日份数据/对比月份数据/对比年份数据")
    private List<BigDecimal> compareData;

    private String platCurrencyName;

    private String currencyCode;

    @Schema(title = "环比上期")
    private String comparedPreviousPeriod ;
    @Schema(title = "均值")
    private String averageValue;

    @Schema(title = "总数")
    private BigDecimal total;





}
