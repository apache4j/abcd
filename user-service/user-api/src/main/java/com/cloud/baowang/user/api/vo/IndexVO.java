package com.cloud.baowang.user.api.vo;


import com.cloud.baowang.common.core.serializer.AppBigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title= "首页信息")
public class IndexVO {


    @Schema(description ="余额" )
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal balance;

    @Schema(description ="冻结金额" )
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal freezeAmount;


    @Schema(description = "平台币可用余额")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal platAvailableAmount;
}
