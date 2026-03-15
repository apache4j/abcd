package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/9/13 14:05
 * @Version: V1.0
 **/
@Schema(description = "平台币-法币互转结果")
@Data
public class SiteCurrencyConvertRespVO {

    @Schema(description = "来源金额")
    private BigDecimal sourceAmount;
    @Schema(description = "来源币种")
    private String sourceCurrencyCode;
    @Schema(description = "转换后金额")
    private BigDecimal targetAmount;
    @Schema(description = "转换后币种")
    private String targetCurrencyCode;
    @Schema(description = "转换汇率")
    private BigDecimal transferRate;

}
