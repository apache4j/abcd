package com.cloud.baowang.agent.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "提款申请小铃铛推送消息vo")
public class WithdrawApplySocketVO implements Serializable {
    @Schema(description = "待审核记录总数")
    private Long pendingCount;
    @Schema(description = "路由")
    private String route;
}
