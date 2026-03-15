package com.cloud.baowang.system.api.api.i18n.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "多语言配置返回DTO")
public class I18NMessageDTO implements Serializable {
    @Schema(title = "i18信息类型")
    private String messageType;
    @Schema(title = "i18信息key")
    private String messageKey;
    @Schema(title = "语言")
    private String language;
    @Schema(title = "信息内容")
    private String message;
}
