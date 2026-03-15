package com.cloud.baowang.system.api.vo.adminLogin;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema(description = "首次登录绑定google")
public class GoogleBindVO {
    @Schema(description =  "管理员ID")
    @NotEmpty(message = ConstantsCode.PARAM_MISSING)
    private String id;

    @Schema(description =  "google密钥")
    @NotEmpty(message = ConstantsCode.PARAM_MISSING)
    private String googleAuthKey;

    @Schema(description =  "校验码")
    @NotEmpty(message = ConstantsCode.Google_MISSING)
    private String verifyCode;

}
