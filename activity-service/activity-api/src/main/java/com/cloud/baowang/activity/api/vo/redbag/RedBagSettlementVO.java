package com.cloud.baowang.activity.api.vo.redbag;

import com.cloud.baowang.common.core.utils.BigDecimalUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Schema(description = "红包雨结算信息")
@AllArgsConstructor
@NoArgsConstructor
public class RedBagSettlementVO {
    @Schema(description = "金额")
    private BigDecimal amount;
    @Schema(description = "红包个数")
    private Integer redbagCount;

    public BigDecimal getAmount() {
        return BigDecimalUtils.formatFourKeep4Dec(this.amount);
    }
}
