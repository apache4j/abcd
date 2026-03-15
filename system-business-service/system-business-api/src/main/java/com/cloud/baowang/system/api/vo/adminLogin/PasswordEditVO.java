package com.cloud.baowang.system.api.vo.adminLogin;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema(description = "站点后台首次修改密码对象")
public class PasswordEditVO {
    @Schema(description = "站点编号")
    @NotEmpty(message = "站点编号不能为空")
    private String siteCode;

    @Schema(description = "用户名")
    @NotEmpty(message = "用户名不能为空")
    private String userName;

    @Schema(description = "新密码")
    @NotEmpty(message = ConstantsCode.NEW_PASSWORD_NULL)
    private String newPassword;

    @Schema(description = "确认密码")
    @NotEmpty(message = ConstantsCode.CONFIRM_PASSWORD_NULL)
    private String confirmPassword;

    @Schema(description = "密码", hidden = true)
    private String password;

}
