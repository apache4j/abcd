package com.cloud.baowang.system.api.vo.language;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "语言管理编辑vo")
public class LanguageManagerEditVO {
    @NotNull(message = ConstantsCode.MISSING_PARAMETERS)
    @Schema(description = "id")
    private String id;
    @Schema(description = "语言名称")
    private String name;
    @Schema(description = "图标")
    private String icon;
}
