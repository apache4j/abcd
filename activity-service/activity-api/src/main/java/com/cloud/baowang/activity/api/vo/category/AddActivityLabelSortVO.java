package com.cloud.baowang.activity.api.vo.category;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "添加活动页签排序规则vo")
@I18nClass
public class AddActivityLabelSortVO {
    @Schema(description = "主键")
    private String id;
    @Schema(description = "活动页签名称")
    @I18nField
    private String labNameI18Code;
    @Schema(description = "启用状态0.禁用，1.启用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;
    @Schema(description = "启用状态")
    private String statusText;
    @Schema(description = "排序，数值越小越靠前")
    private Integer sort;

}
