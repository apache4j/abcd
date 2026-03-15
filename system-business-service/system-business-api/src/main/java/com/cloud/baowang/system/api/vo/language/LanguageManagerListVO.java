package com.cloud.baowang.system.api.vo.language;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

@Data
@I18nClass
@Schema(description = "语言管理详情返回vo")
public class LanguageManagerListVO implements Serializable {
    @Schema(description = "ID")
    private String id;
    @Schema(description = "语言名称")
    private String name;
    @Schema(description = "展示code")
    private String showCode;
    @Schema(description = "站点code")
    private String siteCode;
    @Schema(description = "语言代码")
    private String code;
    @Schema(description = "图标-filekey")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String icon;
    @Schema(description = "图标-url")
    private String iconFileUrl;
    @Schema(description = "排序")
    private Integer sort;
    @Schema(description = "状态 1启用 0禁用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;
    @Schema(description = "状态-文本")
    private String statusText;
}
