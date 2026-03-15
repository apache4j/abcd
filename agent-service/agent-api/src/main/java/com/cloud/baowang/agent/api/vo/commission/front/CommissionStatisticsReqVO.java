package com.cloud.baowang.agent.api.vo.commission.front;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: fangfei
 * @createTime: 2024/11/07 10:50
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "客户端佣金报表统计", description = "客户端佣金报表统计")
public class CommissionStatisticsReqVO {
    @Schema(description = "实时计算开始时间")
    private Long calcStartTime;
    @Schema(description = "实时计算结束时间")
    private Long calcEndTime;
    @Schema(description = "历史期统计开始时间")
    private Long reportStartTime;
    @Schema(description = "历史期统计结束时间")
    private Long reportEndTime;
    @Schema(description = "佣金类型")
    private String commissionType;
    @Schema(description = "周期")
    private Integer settleCycle;
    @Schema(description = "代理id")
    private String agentId;
    @Schema(description = "siteCode")
    private String siteCode;
    @Schema(description = "开始时间")
    private Long startTime;
    @Schema(description = "结束时间")
    private Long endTime;
}
