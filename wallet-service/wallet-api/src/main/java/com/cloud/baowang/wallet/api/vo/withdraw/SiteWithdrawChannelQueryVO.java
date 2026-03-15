package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "站点提款授权返回参数")
@I18nClass
public class SiteWithdrawChannelQueryVO {
    @Schema(description = "提现方式ID")
    private String withdrawWayId;

    @Schema(title = "提款通道id集合")
    private List<String> platform;

    @Schema(title = "提款手续费")
    private BigDecimal withdrawFee;

    @Schema(description = "所属币种")
    private String currencyGroup;

    @Schema(description = "手续费类型")
    private Integer feeType;

    @Schema(description = "手续费-固定金额")
    private BigDecimal wayFeeFixedAmount;
}
