package com.cloud.baowang.report.api.vo.user.complex;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MemberBetInfoVO {
    @Schema( description = "投注金额")
    private BigDecimal betAmount;

    @Schema( description = "有效投注金额")
    private BigDecimal effectiveBetAmount;

    @Schema( description = "币种")
    private String currencyCode ;

    @Schema( description = "投注人数")
    private Integer bettorNums;

    @Schema( description = "注单量")
    private Integer bettingOrderAmount;


}
