package com.cloud.baowang.system.api.vo.adminLogin;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema(description = "Google验证码提交对象")
public class GoogleSubmitVO {
    @Schema(description =  "用戶账号")
    @NotEmpty(message = ConstantsCode.USER_MISSING)
    private String userName;

    @Schema(description =  "用戶密码")
    @NotEmpty(message = ConstantsCode.USER_PASSWORD_NULL)
    private String password;

    @Schema(description =  "校验码")
    private String verifyCode;

    @Schema(description = "谷歌验证秘钥")
    @NotEmpty(message = ConstantsCode.Google_MISSING)
    private String googleAuthKey;

    @Schema(description = "站点编号")
    private String siteCode;

}
