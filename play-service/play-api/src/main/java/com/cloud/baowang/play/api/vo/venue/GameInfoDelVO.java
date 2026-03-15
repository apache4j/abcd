package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "ID对象")
public class GameInfoDelVO {

    @Schema(description = "二级分类ID", required = true)
    private String id;

    @Schema(description = "一级分类ID")
    private String gameOneId;

    @Schema(description = "币种", required = true)
//    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private String currencyCode;

    @Schema(description = "平台CODE")
    private String venueCode;

}
