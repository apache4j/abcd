package com.cloud.baowang.system.api.vo.language;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "语言管理详情请求vo")
public class LanguageManagerInfoReqVO {
    @NotNull(message = ConstantsCode.MISSING_PARAMETERS)
    @Schema(description = "id")
    private String id;
}
