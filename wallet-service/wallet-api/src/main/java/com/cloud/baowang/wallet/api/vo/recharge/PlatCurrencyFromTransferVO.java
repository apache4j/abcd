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
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "平台币转法币参数")
public class PlatCurrencyFromTransferVO {
    @Schema(description = "平台编码")
    private String siteCode;
    @Schema(description = "来源金额")
    private BigDecimal sourceAmt;
    @Schema(description = "目标币种")
    private String targetCurrencyCode;
}
