package com.cloud.baowang.agent.api.vo.merchant;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Schema(description = "发起商务变更vo")
@Data
public class MerchantModifyVO {
    @Schema(description = "站点", hidden = true)
    private String siteCode;

    @Schema(description = "操作人", hidden = true)
    private String operator;

    @Schema(description = "发起类型,目前只有账号状态变更,所以这里直接隐藏后台写死", hidden = true)
    private Integer reviewApplicationType;

    @Schema(description = "账号状态(发起时选择的)")
    private String status;

    @Schema(description = "商务账号")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String merchantAccount;

    @Schema(description = "备注")
    private String applicationInformation;
}
