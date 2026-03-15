package com.cloud.baowang.report.api.vo.userwinlose;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会员输赢统计查询条件")
public class UserWinLossAmountParamVO {

    private String siteCode;
    @Schema( description = "开始时间")
    private Long startTime;

    @Schema( description = "结束时间")
    private Long endTime;

    private List<String> agentIds;
}
