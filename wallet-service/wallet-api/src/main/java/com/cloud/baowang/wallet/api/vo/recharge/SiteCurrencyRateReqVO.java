package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/7/26 14:03
 * @Version: V1.0
 **/
@Data
@Schema(description = "币种汇率配置")
public class SiteCurrencyRateReqVO  {

    @Schema(description = "主键ID")
    private String id;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码",hidden = true)
    private String currencyCode;

    /**
     * 汇率
     */
    @Schema(description = "转换汇率")
    private BigDecimal finalRate;


}
