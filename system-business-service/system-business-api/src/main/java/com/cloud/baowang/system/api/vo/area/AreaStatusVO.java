package com.cloud.baowang.system.api.vo.area;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 启用禁用
 *
 */
@Data
@Schema(title = "区号启用禁用")
public class AreaStatusVO {

    @Schema(title = "id")
    @NotEmpty(message = ConstantsCode.PARAM_MISSING)
    private String id;

    @Schema(title = "状态 1 启用 0 禁用")
    @NotNull(message = ConstantsCode.PARAM_MISSING)
    private Integer status;

    @Schema(title = "站点code",hidden = true)
    private String siteCode;

    @Schema(description = "操作人", hidden = true)
    private String updater;
}
