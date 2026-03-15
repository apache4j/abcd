package com.cloud.baowang.system.api.vo.partner;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "合作赞助商视图对象")
@I18nClass
public class SystemPartnerVO {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "赞助商名称")
    @Size(max = 20, message = "赞助商名称不能超过20个字符")
    private String partnerName;

    @Schema(description = "赞助商图标")
    @NotBlank(message = "赞助商图标不能为空")
    private String partnerIcon;

    @Schema(description = "赞助商图标完整地址-编辑回显用")
    private String partnerIconImage;

    @Schema(description = "启用状态0.禁用，1.启用")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;
    @Schema(description = "启用状态")
    private String statusText;

    @Schema(description = "创建人")
    private String creator;
    @Schema(description = "创建时间")
    private Long createdTime;

    @Schema(description = "操作人")
    private String updater;
    @Schema(description = "修改时间")
    private Long updatedTime;

}
