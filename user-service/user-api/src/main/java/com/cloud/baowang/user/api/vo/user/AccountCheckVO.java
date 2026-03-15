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
@Schema(description = "密码找回请求对象")
public class AccountCheckVO implements Serializable {
    @Schema(description = "账号")
    @NotEmpty(message = "账号不能为空")
    private String userAccount;
}
