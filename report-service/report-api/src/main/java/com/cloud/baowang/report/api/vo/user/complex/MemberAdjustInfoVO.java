package com.cloud.baowang.report.api.vo.user.complex;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MemberAdjustInfoVO {
    @Schema( description = "调整额")
    private BigDecimal totalAdjust;

    @Schema( description = "加额")
    private BigDecimal addAmount ;

    @Schema( description = "币种")
    private String currencyCode ;
    @Schema( description = "加额人数")
    private Integer addAmountPeopleNum;

    @Schema( description = "减额")
    private BigDecimal reduceAmount ;

    @Schema( description = "减额人数")
    private Integer reduceAmountPeopleNums ;



    @Schema( description = "上下分总额")
    private BigDecimal platformTotalAdjust;

    @Schema( description = "上分")
    private BigDecimal platformAddAmount ;

    @Schema( description = "币种WTC")
    private String platformCurrencyCode ;
    @Schema( description = "上分人数")
    private Integer platformAddPeopleNum;

    @Schema( description = "下分")
    private BigDecimal platformReduceAmount ;

    @Schema( description = "下分人数")
    private Integer platformReducePeopleNums ;

}
