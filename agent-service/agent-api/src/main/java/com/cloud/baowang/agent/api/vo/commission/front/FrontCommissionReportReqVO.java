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
@Schema(title = "客户端佣金报表请求对象", description = "客户端佣金报表请求对象")
public class FrontCommissionReportReqVO {
    @Schema(description = "开始时间")
    private Long startTime;
    @Schema(description = "结束时间")
    private Long endTime;
    @Schema(description = "佣金类型")
    private String commissionType;
    @Schema(description = "代理id", hidden = true)
    private String agentId;
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
}
