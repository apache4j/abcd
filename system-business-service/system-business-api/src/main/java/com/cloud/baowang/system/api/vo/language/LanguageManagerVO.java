package com.cloud.baowang.system.api.vo.language;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.checkerframework.checker.units.qual.N;

@Data
@Schema(description = "语言管理返回vo")
@I18nClass
public class LanguageManagerVO {
    @Schema(description = "id")
    private String id;
    @Schema(description = "名称")
    private String name;
    @Schema(description = "展示code")
    private String showCode;
    @Schema(description = "code")
    private String code;
    @Schema(description = "图标-filekey")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;
    @Schema(description = "图标-url")
    private String iconFileUrl;
    @Schema(description = "排序")
    private Integer sort;
    @Schema(description = "总台禁用标识 0总台禁用 1 总台未禁用")
    private Integer centerDisable = 1;
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    @Schema(description = "状态 0启用 1禁用")
    private Integer status;
    @Schema(description = "状态文本 0启用 1禁用")
    private String statusText;
    @Schema(description = "操作时间")
    private Long operateTime;
    @Schema(description = "操作人")
    private String operator;
}
