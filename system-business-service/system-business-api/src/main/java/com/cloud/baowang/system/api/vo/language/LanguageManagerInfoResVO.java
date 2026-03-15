package com.cloud.baowang.system.api.vo.language;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@I18nClass
@Schema(description = "语言管理详情返回vo")
public class LanguageManagerInfoResVO {
    @Schema(description = "id")
    private String id;
    @Schema(description = "语言名称")
    private String name;
    @Schema(description = "展示code")
    private String showCode;
    @Schema(description = "语言代码")
    private String code;
    @Schema(description = "图标-filekey")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;
    @Schema(description = "图标-url")
    private String iconFileUrl;
    @Schema(description = "排序")
    private Integer sort;
}
