package com.cloud.baowang.wallet.api.vo.recharge;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/9/10 20:36
 * @Version: V1.0
 **/
@Data
@Schema(description = "法币转平台币参数")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlatCurrencyToTransferVO {
    @Schema(description = "平台编码")
    private String siteCode;
    @Schema(description = "来源金额")
    private BigDecimal sourceAmt;
    @Schema(description = "来源币种")
    private String sourceCurrencyCode;
}
