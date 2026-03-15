package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "安全中心验证码校验请求对象")
public class SafeVerifyCodeVO implements Serializable {
    @Schema(description = "类型  1  邮箱  2 手机号")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @Schema(title = "验证码")
    @NotEmpty(message = "验证码不能为空")
    private String verifyCode;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;

    @Schema(description = "账号", hidden = true)
    private String userAccount;
}
