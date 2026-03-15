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
@Schema(description = "邮箱手机绑定请求对象")
public class BindAccountVO implements Serializable {
    @Schema(description = "区号, 绑定手机时不能为空，区号和手机分开")
    private String areaCode;

    @Schema(description = "邮箱或者手机号")
    @NotEmpty(message = "邮箱或者手机号不能为空")
    private String account;

    @Schema(description = "类型  1  邮箱  2 手机号")
    @NotNull(message = "类型不能为空")
    private Integer type;

    @Schema(title = "验证码")
    @NotEmpty(message = "验证码不能为空")
    private String verifyCode;

    @Schema(title = "会员账号", hidden = true)
    private String userAccount;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;
}
