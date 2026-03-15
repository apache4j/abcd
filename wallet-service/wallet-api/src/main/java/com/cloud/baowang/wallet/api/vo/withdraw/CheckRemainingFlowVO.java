package com.cloud.baowang.wallet.api.vo.withdraw;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title ="校验会员是否满足提款流水")
public class CheckRemainingFlowVO {


    @Schema(description ="是否满足提款流水 0否 1是")
    private String isWithdraw;

    @Schema(description ="剩余要求流水")
    private BigDecimal remainingFlow;

}
