package com.cloud.baowang.report.api.vo.site;

import com.cloud.baowang.common.core.vo.base.PageVO;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Data
@Schema(description = "平台报表统计查询对象")
public class SiteReportStatisticsQueryPageQueryVO extends PageVO {

    @Schema(description = "统计日期 - 开始时间")
    @NotNull
    private Long startTime;

    @Schema(description = "统计日期 - 结束时间")
    @NotNull
    private Long endTime;

    @Schema(description = "平台编号")
    private String siteCode;

    @Schema(description = "平台名称")
    private String siteName;

    @Schema(description = "平台类型")
    private Integer siteType;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "会员输赢 - 最大值")
    private BigDecimal memberWinLossMax;

    @Schema(description = "会员输赢 - 最小值")
    private BigDecimal memberWinLossMin;

    @Schema(description = "注单量 - 最大值")
    private Long orderCountMax;

    @Schema(description = "注单量 - 最小值")
    private Long orderCountMin;

    @Schema(description = "投注金额 - 最大值")
    private BigDecimal betAmountMax;

    @Schema(description = "投注金额 - 最小值")
    private BigDecimal betAmountMin;

    @Schema(description = "有效投注 - 最大值")
    private BigDecimal validBetMax;

    @Schema(description = "有效投注 - 最小值")
    private BigDecimal validBetMin;

    @Schema(description = "存款金额 - 最大值")
    private BigDecimal depositAmountMax;

    @Schema(description = "存款金额 - 最小值")
    private BigDecimal depositAmountMin;

    @Schema(description = "取款金额 - 最大值")
    private BigDecimal withdrawalAmountMax;

    @Schema(description = "取款金额 - 最小值")
    private BigDecimal withdrawalAmountMin;

    @Schema(description = "是否用平台币兑换查看0.否,1.是")
    private Boolean isPlantShow;
}
