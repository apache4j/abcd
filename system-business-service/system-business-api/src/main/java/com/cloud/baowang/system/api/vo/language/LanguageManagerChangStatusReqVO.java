package com.cloud.baowang.system.api.vo.language;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "语言管理状态变更vo")
public class LanguageManagerChangStatusReqVO {
    @NotBlank(message = ConstantsCode.MISSING_PARAMETERS)
    @Schema(description = "id")
    private String id;
    @NotNull(message = ConstantsCode.MISSING_PARAMETERS)
    @Schema(description = "状态 0启用 1禁用")
    private Integer status;
}
