package com.cloud.baowang.agent.api.vo.withdrawConfig;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "代理提款金额配置")
public class WithdrawConfigVO {

    @Schema(description = "币种")
    private String currency;

    @Schema(title = "大额提款标记金额")
    @NotNull(message = "大额提款标记金额不能为空")
    private BigDecimal largeWithdrawMarkAmount = BigDecimal.ZERO;

    @Schema(title = "单次提款最低限额")
    @NotNull(message = "单次提款最低限额不能为空")
    private BigDecimal withdrawMinQuotaSingle = BigDecimal.ZERO;

    @Schema(title = "单次提款最高限额")
    @NotNull(message = "单次提款最高限额不能为空")
    private BigDecimal withdrawMaxQuotaSingle = BigDecimal.ZERO;

    @Schema(title = "单日最高提款次数")
    @NotNull(message = "单日最高提款次数不能为空")
    private Integer withdrawMaxCountDay;

    @Schema(title = "单日最高提款总额")
    @NotNull(message = "单日最高提款总额不能为空")
    private BigDecimal withdrawMaxQuotaDay = BigDecimal.ZERO;

    @Schema(title = "费率")
    @NotBlank(message = "费率不能为空")
    private BigDecimal feeRate = BigDecimal.ZERO;

    @Schema(title = "手续费类型 0-百分比手续费 1-固定金额手续费")
    @NotBlank(message = "手续费类型不能为空")
    private String feeType;

    @Schema(title = "提款方式ID")
    @NotNull(message = "提款方式不能为空")
    private String withdrawWayId;

}
