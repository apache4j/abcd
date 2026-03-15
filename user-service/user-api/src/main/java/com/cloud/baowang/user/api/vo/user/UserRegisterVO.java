package com.cloud.baowang.user.api.vo.user;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户注册请求对象")
public class UserRegisterVO implements Serializable {
    @Schema(description = "账号")
    @NotEmpty(message = ConstantsCode.USER_MISSING)
    private String userAccount;

    @Schema(description = "密码")
    @NotEmpty(message = ConstantsCode.USER_PASSWORD_NULL)
    private String password;

    @Schema(description = "确认登录密码密码")
    @NotEmpty(message = ConstantsCode.CONFIRM_PASSWORD_NULL)
    private String confirmPassword;

    @Schema(title = "主货币")
    @NotEmpty(message = ConstantsCode.USER_MAIN_CURRENCY_NOT_NULL)
    private String mainCurrency;

    @Schema(title = "推荐码")
    private String inviteCode;

    /**
     * 设备id
     */
    @Schema(description = "设备号")
    private String deviceNo;

    @Schema(description = "ip", hidden = true)
    private String ip;

    @Schema(description = "验证ID，verifyCode 接口中的参数")
    private String certifyId;

    @Schema(description = "设备版本")
    private String deviceVersion;

    @Schema(description = "登录地址")
    private String loginAddress;

    @Schema(description = "版本号", hidden = true)
    private String version;

    @Schema(description = "eventId(前端生成)")
    private String eventId;

}
