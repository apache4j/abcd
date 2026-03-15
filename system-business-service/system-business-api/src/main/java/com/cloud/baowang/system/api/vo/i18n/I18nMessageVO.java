package com.cloud.baowang.system.api.vo.i18n;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "多语言配置VO")
public class I18nMessageVO {
    @Schema(description = "id")
    private String id;
    @Schema(description = "类型")
    private String messageType;
    @Schema(description = "键值")
    private String messageKey;
    @Schema(description = "语言")
    private String language;
    @Schema(description = "内容")
    private String message;
}
