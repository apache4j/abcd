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
@Schema(description = "设置交易密码请求对象")
public class SetWithdrawalPasswordReqVO implements Serializable {
    @Schema(description = "新密码")
    @NotEmpty(message = "新密码不能为空")
    private String password;

    @Schema(description = "确认密码")
    @NotEmpty(message = ConstantsCode.CONFIRM_PASSWORD_NULL)
    private String confirmPassword;

    @Schema(description = "用户账号", hidden = true)
    private String userAccount;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;
}
