package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@I18nClass
@Schema(description = "一级分类添加场馆请求对象")
public class GameOneClassCurrencyListVO {

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "场馆")
    private List<GameOneClassVenueInfoVO> venueCodeList;


}
