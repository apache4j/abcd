package com.cloud.baowang.system.api.vo.operations;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Schema(description = "IP归属币种请求类")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class IpAddressAreaCurrencyStatusReqVO  implements Serializable {

    @Schema(description = "id")
    @NotNull(message = ConstantsCode.PARAM_MISSING)
    private String id;

    /**
     *  状态: (1 开启中 2 维护中 3 已禁用)
     */
    @Schema(description = "状态: (1 开启 0 禁用)")
    @NotNull(message = ConstantsCode.PARAM_MISSING)
    private Integer status;

}
