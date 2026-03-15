package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "找回交易密码请求对象")
public class ReFindWPwdVO implements Serializable {
    @Schema(description = "类型  1  邮箱  2 手机号")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @Schema(description = "登录密码")
    @NotEmpty(message = "登录密码不能为空")
    private String password;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;

    @Schema(description = "账号", hidden = true)
    private String userAccount;
}
