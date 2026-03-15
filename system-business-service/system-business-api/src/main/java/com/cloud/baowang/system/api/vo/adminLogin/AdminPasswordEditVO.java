package com.cloud.baowang.system.api.vo.adminLogin;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema(description = "总控后台首次修改密码对象")
public class AdminPasswordEditVO {
    @Schema(description = "管理员id")
    @NotEmpty(message = "管理员id不能为空")
    private String id;

    @Schema(description = "新密码")
    @NotEmpty(message = "新密码不能为空")
    private String newPassword;

    @Schema(description = "确认密码")
    @NotEmpty(message = ConstantsCode.CONFIRM_PASSWORD_NULL)
    private String confirmPassword;

    @Schema(description = "密码", hidden = true)
    private String password;

}
