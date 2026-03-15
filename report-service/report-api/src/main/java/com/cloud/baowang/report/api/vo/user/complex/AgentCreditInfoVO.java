package com.cloud.baowang.report.api.vo.user.complex;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AgentCreditInfoVO {

    @Schema( description = "额度")
    private BigDecimal credit;

    @Schema( description = "币种")
    private String currencyCode ;

    @Schema( description = "额度-人数")
    private Integer creditPeopleNums;

    @Schema( description = "额度-次数")
    private Integer creditTimes;
}
