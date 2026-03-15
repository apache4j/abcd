package com.cloud.baowang.play.api.vo.user;


import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayUserDataDetailVO {

    @Schema(description = "总胜利")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal totalWins;

    @Schema(description = "总注单")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal totalBet;

    @Schema(description = "总注单条数")
    private Long totalBetCount;
}
