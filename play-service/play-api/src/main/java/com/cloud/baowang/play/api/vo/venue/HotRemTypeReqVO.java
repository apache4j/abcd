package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "热门推荐类型-入参数")
@I18nClass
public class HotRemTypeReqVO {

    @Schema(description = "一级分类ID 首页:front_page")
    private String gameOneClassId;

    @Schema(description = "币种")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private String currencyCode;


}
