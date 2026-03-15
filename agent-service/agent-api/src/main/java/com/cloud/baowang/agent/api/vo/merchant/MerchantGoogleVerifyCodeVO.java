package com.cloud.baowang.agent.api.vo.merchant;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "商务验证码校验请求对象")
public class MerchantGoogleVerifyCodeVO implements Serializable {
    @Schema(description = "商务账号")
    @NotEmpty(message = ConstantsCode.AGENT_MISSING)
    private String merchantAccount;

    @Schema(title = "验证码")
    @NotEmpty(message = ConstantsCode.CODE_IS_EMPTY)
    private String verifyCode;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;
}
