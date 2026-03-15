package com.cloud.baowang.agent.api.vo.agent.clienthome;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: kimi
 */
@Data
@Schema(description = "数据对比 曲线图 VO")
public class DataCompareGraphVO {
    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "横坐标 每天")
    private List<String> dayList;

    @Schema(description = "本月数据")
    private List<BigDecimal> currentMonthData;

    @Schema(description = "对比月份数据")
    private List<BigDecimal> compareMonthData;
}
