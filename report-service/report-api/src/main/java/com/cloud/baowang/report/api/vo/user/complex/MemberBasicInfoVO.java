package com.cloud.baowang.report.api.vo.user.complex;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MemberBasicInfoVO {
    @Schema(description = "金额")
    private BigDecimal amount;

    private String currencyCode ;

    @Schema(description = "人数")
    private Integer peopleNums;

}
