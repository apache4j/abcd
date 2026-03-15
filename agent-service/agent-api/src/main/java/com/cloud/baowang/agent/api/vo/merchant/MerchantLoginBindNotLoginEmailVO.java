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
@Schema(description = "绑定邮箱请求对象-无需登录")
public class MerchantLoginBindNotLoginEmailVO implements Serializable {
    @Schema(description = "验证码")
    @NotEmpty(message = ConstantsCode.SMS_CODE_IS_NULL)
    private String code;

    @Schema(description = "邮箱")
    @NotEmpty(message = ConstantsCode.USER_EMAIL_IS_EMPTY)
    private String email;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;

    @Schema(description = "商务账号")
    private String merchantAccount;

    
}
