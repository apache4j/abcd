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
@Schema(description = "发送邮箱验证码请求对象")
public class MerchantLoginChangePasswordVO implements Serializable {
    @Schema(description = "旧密码")
    @NotEmpty(message = ConstantsCode.NOT_EMPTY_PASSWORD)
    private String oldPassword;

    @Schema(description = "新密码")
    @NotEmpty(message = ConstantsCode.NOT_EMPTY_NEW_PASSWORD)
    private String newPassword  ;

    @Schema(description = "确认密码")
    @NotEmpty(message = ConstantsCode.NOT_EMPTY_CONFIRM_PASSWORD)
    private String confirmPassword;

    private String siteCode;
}
