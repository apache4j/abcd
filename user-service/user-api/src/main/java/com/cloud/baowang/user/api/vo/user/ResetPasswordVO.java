package com.cloud.baowang.user.api.vo.user;

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
@Schema(description = "密码找回请求对象")
public class ResetPasswordVO implements Serializable {
    @Schema(description = "账号")
    @NotEmpty(message = "账号不能为空")
    private String userAccount;

    @Schema(description = "邮箱或者手机号")
    @NotEmpty(message = "邮箱或者手机号不能为空")
    private String account;

    @Schema(description = "新密码")
    @NotEmpty(message = ConstantsCode.NEW_PASSWORD_NULL)
    private String newPassword;

    @Schema(description = "确认密码")
    @NotEmpty(message = ConstantsCode.CONFIRM_PASSWORD_NULL)
    private String confirmPassword;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;
}
