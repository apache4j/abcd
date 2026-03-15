package com.cloud.baowang.agent.api.vo.commission;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@Schema(title = "佣金模拟计算请求对象")
public class CommissionImitateCalcVO implements Serializable {
    /*@Schema(title = "有效新增活跃会员数", hidden = true)
    private Integer newActiveNumber;*/

    @Schema(title = "有效活跃人数")
    @NotNull(message = "有效活跃人数不能为空")
    private Integer activeNumber;

    @Schema(title = "本月团队总输赢")
    @NotNull(message = "本月团队总输赢不能为空")
    private BigDecimal winLossAmount;

    @Schema(title = "场馆code")
    @NotEmpty(message = "场馆code不能为空")
    private String venueCode;

    @Schema(title = "存提总金额")
    @NotNull(message = "存提总金额不能为空")
    private BigDecimal depWithdrawAmount;

    @Schema(title = "代理账号", hidden = true)
    private String agentAccount;

    @Schema(title = "siteCode", hidden = true)
    private String siteCode;

}
