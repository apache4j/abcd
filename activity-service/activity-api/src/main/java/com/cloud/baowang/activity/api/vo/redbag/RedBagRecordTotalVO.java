package com.cloud.baowang.activity.api.vo.redbag;

import com.cloud.baowang.common.core.utils.BigDecimalUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Schema(description = "红包雨用户获取记录总计")
@AllArgsConstructor
@NoArgsConstructor
public class RedBagRecordTotalVO {
    @Schema(description = "活动baseId")
    private String baseId;
    @Schema(description = "红包总金额")
    private BigDecimal totalAmount = BigDecimal.ZERO;
    @Schema(description = "红包个数")
    private Integer count = 0;

    public BigDecimal getTotalAmount() {
        return BigDecimalUtils.formatFourKeep4Dec(this.totalAmount);
    }
}
