package com.cloud.baowang.report.api.vo.user.complex;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class MemberWithdrawInfoVO {
    @Schema( description = "总取款")
    private BigDecimal totalWithdraw;

    @Schema( description = "取款人数")
    private Integer withdrawPeopleNums ;

    @Schema( description = "币种")
    private String currencyCode ;

    @Schema( description = "取款次数")
    private Integer withdrawNums ;

    @Schema( description = "大额取款人数")
    private Integer largeWithdrawPeopleNums ;

    @Schema( description = "大额取款次数")
    private Integer largeWithdrawNums ;

}
