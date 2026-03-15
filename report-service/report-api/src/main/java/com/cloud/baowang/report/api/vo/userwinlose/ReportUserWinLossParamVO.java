package com.cloud.baowang.report.api.vo.userwinlose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员查询相关报表请求对象")
public class ReportUserWinLossParamVO {
    @Schema( description = "开始时间")
    private Long startTime;

    @Schema( description = "结束时间")
    private Long endTime;

    private String userId;

    private List<String> userIds;

    @Schema( description = "需要查询的上级id，如果需要考虑溢出/转代就传此参数")
    private String agentId;
}
