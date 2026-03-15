package com.cloud.baowang.wallet.api.vo.userWithdrawRecord;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "代理查询存提金额对象")
public class AgentDepositWithQueryVO {
    @Schema( description = "开始时间")
    private Long startTime;

    @Schema( description = "结束时间")
    private Long endTime;

    private Integer type;

    private List<String> agentIds;

    private String siteCode;
}
