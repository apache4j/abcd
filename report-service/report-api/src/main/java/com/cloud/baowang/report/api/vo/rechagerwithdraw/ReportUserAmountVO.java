package com.cloud.baowang.report.api.vo.rechagerwithdraw;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/11 16:36
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Builder
@Schema(title = "会员存取款汇总对象")
public class ReportUserAmountVO {
    @Schema(description = "userId")
    private String userId;
    @Schema(description = "存款金额")
    private BigDecimal rechargeAmount;
    @Schema(description = "取款金额")
    private BigDecimal withdrawAmount;
/*    @Schema(description = "方式手续费")
    private BigDecimal wayFeeAmount;*/
    /**
     * 结算手续费
     */
    private BigDecimal settleFeeAmount;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "存取款类型 1 存款  2 取款")
    private Integer type;
}
