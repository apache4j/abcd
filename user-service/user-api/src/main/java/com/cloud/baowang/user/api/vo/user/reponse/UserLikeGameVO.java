package com.cloud.baowang.user.api.vo.user.reponse;

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
public class UserLikeGameVO {


    @Schema(description = "游戏名称")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private String name;

    @Schema(description = "图标")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private String pcIcon;

    @Schema(description = "投注")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal totalBet;

}
