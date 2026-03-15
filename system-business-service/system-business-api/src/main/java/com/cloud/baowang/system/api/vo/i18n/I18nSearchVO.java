package com.cloud.baowang.system.api.vo.i18n;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Schema(description = "多语言搜索VO")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class I18nSearchVO {
    @Schema(description = "业务内容搜索前缀")
    private String bizKeyPrefix;


    @Schema(description = "业务内容搜索前缀")
    private List<String> bizKeyPrefixList;

    @Schema(description = "搜索内容 模糊搜索 只能传一个")
    private String searchContent;
    @Schema(description = "搜索内容 精确搜索 只能传一个")
    private String exactSearchContent;
    @Schema(description = "语言")
    private String lang;
}
