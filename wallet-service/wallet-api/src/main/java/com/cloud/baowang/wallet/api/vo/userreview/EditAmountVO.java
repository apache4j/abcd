package com.cloud.baowang.wallet.api.vo.userreview;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: wade
 */
@Data
 @Schema(title = "充值编辑确认 Request")
public class EditAmountVO {

    @Schema(title  = "id")
    @NotNull(message = "id不能为空")
    private Long id;

    @Schema(title  = "实际到账金额")
    @NotNull(message = "实际到账金额不能为空")
    private BigDecimal arriveAmount;
}
