package com.cloud.baowang.user.api.vo.user.request;

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
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "用户输赢查询VO")
public class UserWinLossReqVO {
    @Schema(title = "站点编码")
    private String siteCode;
    @Schema(title = "用户Id")
    private String userId;
    @Schema(title = "场馆code")
    private String venueCode;

    private List<String> venueCodeList;
    @Schema(title = "开始时间戳")
    private Long startTime;
    @Schema(title = "结束时间戳")
    private Long endTime;

    @Schema(title = "流水汇总区间开始")
    private BigDecimal startTotalBetAmount;

    @Schema(title = "流水汇总区间结束")
    private BigDecimal endTotalBetAmount;

    @Schema(title = "当天起始时间戳")
    private Long dayMillis;

    @Schema(title = "当天起始时间戳范围开始")
    private Long dayMillisStartTime;

    @Schema(title = "当天起始时间戳范围结束")
    private Long dayMillisEndTime;


}
