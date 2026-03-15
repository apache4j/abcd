package com.cloud.baowang.report.api.vo;

import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author: fangfei
 * @createTime: 2024/09/10 9:56
 * @description: 排行榜请求VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "排行榜请求VO")
public class ReportUserTopReqVO extends PageVO {
    @Schema(title = "站点编码")
    private String siteCode;
    @Schema(title = "场馆code")
    private String venueCode;
    @Schema(title = "开始时间戳")
    private Long startTime;
    @Schema(title = "结束时间戳")
    private Long endTime;
    @Schema(title = "最小输赢金额")
    private BigDecimal winLossAmountMin;
    @Schema(title = "最大输赢金额")
    private BigDecimal winLossAmountMax;
    @Schema(title = "最小投注额")
    private BigDecimal betAmountMin;
    @Schema(title = "最大投注额")
    private BigDecimal betAmountMax;
    @Schema(title = "用户列表集合")
    private List<String> userIdList;
    @Schema(title = "场馆code集合")
    private List<String> venueCodeList;
    @Schema(title = "用户")
    private String userId;

    @Schema(title = "币种", hidden = true)
    private String currencyCode;

    @Schema(title = "场馆类型", hidden = true)
    private Integer venueType;

    @Schema(title = "开始时间戳", hidden = true)
    private Long startDayMillisTime;

    @Schema(title = "结束时间戳", hidden = true)
    private Long endDayMillisTime;

    @Schema(title = "当天开始时间戳", hidden = true)
    private Long dayMillis;

    @Schema(title = "平台币种转换后的金额", hidden = true)
    private BigDecimal platformDayBetAmount;

    @Schema(title = "天数", hidden = true)
    private Integer countDay;
}