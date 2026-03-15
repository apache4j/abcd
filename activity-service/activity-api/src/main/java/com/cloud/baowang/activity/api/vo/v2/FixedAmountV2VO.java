package com.cloud.baowang.activity.api.vo.v2;

import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Schema(description = "固定金额VO，包含存款和赠送金额信息")
@Data
public class FixedAmountV2VO {

    @Schema(description = "币种")
    private String currency;

    private List<AmountV2VO> amount;

}