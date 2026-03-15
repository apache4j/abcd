package com.cloud.baowang.agent.api.vo.merchant;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
@Schema(description = "新增商务 vo")
public class AddMerchantVO implements Serializable {
    @Schema(description = "站点", hidden = true)
    private String siteCode;
    @Schema(description = "商务账号")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String merchantAccount;
    @Schema(description = "商务名称")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String merchantName;
    @Schema(description = "商务密码")
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String merchantPassword;
    @Schema(description = "申请信息")
    private String applicationRemark;
    @Schema(description = "申请人", hidden = true)
    private String application;
}
