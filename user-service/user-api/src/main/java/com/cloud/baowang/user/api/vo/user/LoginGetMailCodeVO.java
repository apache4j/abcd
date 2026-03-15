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
@Schema(description = "发送邮箱验证码请求对象")
public class LoginGetMailCodeVO implements Serializable {
    @Schema(description = "账号")
    @NotEmpty(message = "账号不能为空")
    private String userAccount;

    @Schema(description = "邮箱")
    @NotEmpty(message = ConstantsCode.USER_EMAIL_IS_EMPTY)
    private String email;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;
}
