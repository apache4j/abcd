package com.cloud.baowang.system.api.vo.language;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@Builder
@Schema(description = "语言管理新增vo")
public class LanguageManagerAddVO {
    @Schema(description = "站点code")
    private String siteCode;
    @Schema(description = "语言code")
    private List<String> codeList;
}
