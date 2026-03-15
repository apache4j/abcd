package com.cloud.baowang.report.api.vo.user.complex;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MemberWinOrLoseVO {
    @Schema(description = "会员输赢")
    private BigDecimal memberWinOrLose;

    @Schema( description = "币种")
    private String currencyCode ;


    @Schema(description = "打赏金额")
    private BigDecimal tipsAmount;





}
