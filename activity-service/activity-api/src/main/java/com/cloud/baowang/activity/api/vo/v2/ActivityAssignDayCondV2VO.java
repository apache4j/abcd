package com.cloud.baowang.activity.api.vo.v2;

import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Schema(description = "指定存款日期匹配条件 百分比时传递 minDepositAmt,maxDepositAmt,acquireNum,acquireAmount")
@Data
public class ActivityAssignDayCondV2VO implements Serializable {

    List<AssignDayCondV2VO> amount;

    @Schema(description = "场馆类型")
    private String venueCode;
    @Schema(description = "pp游戏code")
    private String accessParameters;
    @Schema(description = "限注金额")
    private BigDecimal betLimitAmount;

    @Schema(description = "币种")
    private String currencyCode;
}
