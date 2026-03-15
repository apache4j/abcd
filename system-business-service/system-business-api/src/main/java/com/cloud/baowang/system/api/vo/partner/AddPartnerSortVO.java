package com.cloud.baowang.system.api.vo.partner;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "添加赞助商排序规则视图")
@I18nClass
public class AddPartnerSortVO {
    @Schema(description = "主键")
    private String id;

    @Schema(description = "赞助商名称")
    @NotBlank(message = "赞助商名称不能为空")
    private String partnerName;

    @Schema(description = "启用状态0.禁用，1.启用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;
    @Schema(description = "启用状态")
    private String statusText;
    @Schema(description = "排序，数值越小越靠前")
    private Integer sort;

    @Schema(description = "操作人", hidden = true)
    private String updater;
    @Schema(description = "修改时间", hidden = true)
    private Long updatedTime;
}
