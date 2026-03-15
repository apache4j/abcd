package com.cloud.baowang.system.api.vo.dict;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 系统字典配置表 PO 类
 */
@Data
@Schema(description = "系统字典配置vo")
@I18nClass
public class SystemDictConfigReqVO {
    @Schema(description = "id")
    private String id;
    /**
     * 配置参数
     */
    @Schema(description = "配置参数")
    @NotBlank(message = ConstantsCode.PARAM_MISSING)
    private String configParam;
    /**
     * 操作人
     */
    @Schema(description = "操作人", hidden = true)
    private String operator;

    @Schema(description = "siteCode", hidden = true)
    private String siteCode;

}
