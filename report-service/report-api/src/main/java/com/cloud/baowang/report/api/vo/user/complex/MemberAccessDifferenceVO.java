package com.cloud.baowang.report.api.vo.user.complex;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MemberAccessDifferenceVO {
    @Schema( description = "会员存取差")
    private BigDecimal memberAccessDifference;

    @Schema( description = "币种")
    private String currencyCode ;


}
