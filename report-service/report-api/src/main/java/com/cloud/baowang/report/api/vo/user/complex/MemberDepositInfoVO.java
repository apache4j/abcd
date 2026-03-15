package com.cloud.baowang.report.api.vo.user.complex;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MemberDepositInfoVO {
    @Schema( description = "总存款")
    private BigDecimal totalDeposit;

    @Schema( description = "币种")
    private String currencyCode ;

    @Schema( description = "存款人数")
    private Integer depositPeopleNums ;

    @Schema( description = "存款次数")
    private Integer depositNums ;



}
