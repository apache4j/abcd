package com.cloud.baowang.common.core.vo.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@Accessors(chain = true)
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
public class I18nMsgFrontVO implements Serializable {
    @Schema(description = "i18n key")
    private String messageKey;
    @Schema(description = "信息内容")
    private String message;
    @Schema(description = "信息内容")
    private String messageFileUrl;
    @Schema(description = "语言")
    private String language;
    //语言信息
    @Schema(description = "语言名称")
    private String languageName;
    @Schema(description = "展示code")
    private String showCode;
    @Schema(description = "展示iconUrl")
    private String iconUrl;

    public I18nMsgFrontVO(String language, String message) {
        this.language = language;
        this.message = message;
    }
}
