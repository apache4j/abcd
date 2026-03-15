package com.cloud.baowang.report.api.vo.user.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserWinLoseRecalculateDetailVO {
    @Schema(description = "用户账号")
    private String userId;
    @Schema(description = "代理账号")
    private String agentId;
    @Schema(description = "用户当天查询项返回金额")
    private BigDecimal amount;
}
