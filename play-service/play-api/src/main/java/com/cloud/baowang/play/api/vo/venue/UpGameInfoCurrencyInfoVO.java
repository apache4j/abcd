package com.cloud.baowang.play.api.vo.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpGameInfoCurrencyInfoVO {

    @Schema(description = "场馆名称")
    private String venueName;

    @Schema(description = "场馆CODE")
    private String venueCode;

    @Schema(description = "游戏ID列表")
    private List<String> gameIdList;

    @Schema(description = "币种")
    private List<String> currencyCodeList;
}
