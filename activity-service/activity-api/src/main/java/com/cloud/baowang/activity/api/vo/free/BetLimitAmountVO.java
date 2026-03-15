package com.cloud.baowang.activity.api.vo.free;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @className: BetLimitAmountVO
 * @author: wade
 * @description: 币种限额
 * @date: 13/6/25 10:11
 */
@Data
public class BetLimitAmountVO {

    @Schema(title = "币种")
    private String currency;
    @Schema(title = "限注金额")
    private BigDecimal betLimitAmount;
}
