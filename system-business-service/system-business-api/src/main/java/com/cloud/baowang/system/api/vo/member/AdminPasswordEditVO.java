package com.cloud.baowang.system.api.vo.member;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema(description = "总控后台登录密码修改对象")
public class AdminPasswordEditVO {

    @Schema(description = "id", hidden = true)
    private String id;

    @Schema(description = "管理员name", hidden = true)
    private String userName;

    @Schema(description = "旧密码")
    @NotEmpty(message = ConstantsCode.OLD_PASSWORD_NULL)
    private String oldPassword;

    @Schema(description = "新密码")
    @NotEmpty(message = ConstantsCode.NEW_PASSWORD_NULL)
    private String newPassword;

    @Schema(description = "确认密码")
    @NotEmpty(message = ConstantsCode.CONFIRM_PASSWORD_NULL)
    private String confirmPassword;

    @Schema(description =  "Google验证码")
    private String verifyCode;
}
