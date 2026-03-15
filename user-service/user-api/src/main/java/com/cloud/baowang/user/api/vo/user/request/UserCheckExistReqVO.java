package com.cloud.baowang.user.api.vo.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "判断是否存在已注册账号")
public class UserCheckExistReqVO implements Serializable {
    @Schema(description = "账号")
    private String userAccount;

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号码")
    @NotEmpty(message = "手机号码不能为空")
    private String phone;
}
