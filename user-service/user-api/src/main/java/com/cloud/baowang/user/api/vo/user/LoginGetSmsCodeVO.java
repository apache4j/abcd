package com.cloud.baowang.user.api.vo.user;

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
public class LoginGetSmsCodeVO implements Serializable {
    @Schema(description = "账号")
    @NotEmpty(message = "账号不能为空")
    private String userAccount;

    @Schema(description = "手机号码")
    @NotEmpty(message = "手机号码不能为空")
    private String phone;

    @Schema(description = "区号")
    @NotEmpty(message = "区号不能为空")
    private String areaCode;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;
}
