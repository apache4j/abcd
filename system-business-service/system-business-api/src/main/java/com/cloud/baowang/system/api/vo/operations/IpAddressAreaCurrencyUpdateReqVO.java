package com.cloud.baowang.system.api.vo.operations;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(description = "IP归属币种请求类")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IpAddressAreaCurrencyUpdateReqVO extends PageVO {

    @Schema(description = "id")
    @NotNull(message = ConstantsCode.PARAM_MISSING)
    private String id;

    @NotNull(message = ConstantsCode.PARAM_MISSING)
    @Schema(description = "分类名称")
    private String categoryName;

    @NotNull(message = ConstantsCode.PARAM_MISSING)
    @Schema(description = "包含国家")
    private List<AreaVO> areaNameList;

    @NotNull(message = ConstantsCode.PARAM_MISSING)
    @Schema(description = "映射币种")
    private String currencyCode;

    @NotNull(message = ConstantsCode.PARAM_MISSING)
    @Schema(description = "映射币种")
    private String currencyName;

    //@NotNull(message = ConstantsCode.PARAM_MISSING)
    @Schema(description = "优先级")
    private Integer orderSort;

    @Schema(description = " ")
    private String creator;


    @Schema(description = " ")
    private String updater;

    @Schema(description = "备注")
    private String remark;


}
