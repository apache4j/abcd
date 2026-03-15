package com.cloud.baowang.play.api.vo.venue;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "热门推荐类型-入参数")
@I18nClass
public class UpHotRemTypeReqVO {

    @Schema(description = "一级分类ID 首页:front_page")
    private String gameOneClassId;

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "true=热门排序,false=一级分类排序")
    private Boolean hotType;

    @NotNull(message = ConstantsCode.PARAM_ERROR)
    @Schema(description = "币种-分类顺序数组")
    private List<UpHotRemTypeCurrencyReqVO> list;


}
