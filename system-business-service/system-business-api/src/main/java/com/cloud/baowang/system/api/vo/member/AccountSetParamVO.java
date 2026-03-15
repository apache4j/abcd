package com.cloud.baowang.system.api.vo.member;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


/**
 * @author qiqi
 */
@Data
@Schema(description = "账户设置请求对象")
public class AccountSetParamVO {

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "旧密码", required = true)
    @NotEmpty(message = "旧密码不能为空")
    private String oldPassword;

    @Schema(description = "新密码", required = true)
    @NotEmpty(message = "新密码不能为空")
    private String newPassword;

    @Schema(description = "确认新密码", required = true)
    @NotEmpty(message = "确认新密码不能为空")
    private String confirmNewPassword;

}
