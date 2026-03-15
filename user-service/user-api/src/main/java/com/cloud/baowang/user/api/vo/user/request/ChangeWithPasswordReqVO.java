package com.cloud.baowang.user.api.vo.user.request;

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
@Schema(description = "找回交易密码请求对象")
public class ChangeWithPasswordReqVO implements Serializable {

    @Schema(description = "新密码")
    @NotEmpty(message = ConstantsCode.NEW_PASSWORD_NULL)
    private String newPassword;

    @Schema(description = "确认密码")
    @NotEmpty(message = ConstantsCode.CONFIRM_PASSWORD_NULL)
    private String confirmPassword;

    @Schema(title = "验证码")
    private String verifyCode;

    @Schema(description = "用户账号", hidden = true)
    private String userAccount;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;
}
