package com.cloud.baowang.activity.api.vo.free;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AddFreeGameConfigDTO {
    private String userAccount;

    private Integer acquireNum;

    private String currency;

    @Schema(title = "限注金额")
    private BigDecimal betLimitAmount;


}
