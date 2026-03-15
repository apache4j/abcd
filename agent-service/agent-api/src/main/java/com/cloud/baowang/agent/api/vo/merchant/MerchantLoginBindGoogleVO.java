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
@Schema(description = "绑定google请求对象")
public class MerchantLoginBindGoogleVO implements Serializable {
    @Schema(description = "google验证码")
    @NotEmpty(message = ConstantsCode.MERCHAT_MISSING)
    private String code;

    @Schema(description = "密码")
    @NotEmpty(message = ConstantsCode.USER_EMAIL_IS_EMPTY)
    private String password;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;

    @Schema(description = "google auth key")
    private String googleAuthKey;

    
}
