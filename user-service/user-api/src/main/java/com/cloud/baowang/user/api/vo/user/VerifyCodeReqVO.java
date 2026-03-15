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
@Schema(description = "验证码校验请求对象")
public class VerifyCodeReqVO implements Serializable {
    @Schema(description = "token")
    @NotEmpty(message = "token不能为空")
    private String verifyToken;
}
