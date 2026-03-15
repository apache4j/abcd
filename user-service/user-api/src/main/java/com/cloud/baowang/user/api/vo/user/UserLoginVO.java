package com.cloud.baowang.user.api.vo.user;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.vo.IPRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户登录请求对象")
public class UserLoginVO implements Serializable {
    @Schema(description = "账号")
    @NotEmpty(message = "账号不能为空")
    private String userAccount;

    @Schema(description = "密码")
    @NotEmpty(message = ConstantsCode.USER_PASSWORD_NULL)
    private String password;

    @Schema(description = "设备号")
    private String deviceNo;

    @Schema(description = "ip", hidden = true)
    private String ip;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;


    @Schema(description = "是否是提交验证码，辅助参数", hidden = true)
    private Boolean submitKey;

    @Schema(description = "验证ID，verifyCode 接口中的参数")
    private String certifyId;

    @Schema(description = "设备版本")
    private String deviceVersion;

    @Schema(description = "是否是注册登录", hidden = true)
    private Boolean isRegister;

    @Schema(description = "登录地址")
    private String loginAddress;

    @Schema(description = "登录状态 是否第一次登录")
    private Boolean firstLogin;

    @Schema(description = "版本号")
    private String version;

    @Schema(description = "设备类型(app使用)")
    private String deviceType;

    private IPRespVO ipApiVO;
}
