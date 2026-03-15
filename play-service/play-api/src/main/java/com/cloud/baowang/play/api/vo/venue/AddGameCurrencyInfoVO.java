package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "批量新增币种对象")
public class AddGameCurrencyInfoVO {

    @Schema(description = "批量修改ID", required = true)
    @NotEmpty(message = ConstantsCode.PARAM_ERROR)
    private List<String> idBatch;


    @Schema(description = "币种")
    private List<String> currencyList;


}
