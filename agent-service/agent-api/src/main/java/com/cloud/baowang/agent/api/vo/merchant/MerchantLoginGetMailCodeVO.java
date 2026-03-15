package com.cloud.baowang.agent.api.vo.merchant;

import com.cloud.baowang.common.core.constants.ConstantsCode;
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
public class MerchantLoginGetMailCodeVO implements Serializable {
    @Schema(description = "商务账号")
    @NotEmpty(message = ConstantsCode.MERCHAT_MISSING)
    private String merchantAccount;

    @Schema(description = "邮箱")
    @NotEmpty(message = ConstantsCode.USER_EMAIL_IS_EMPTY)
    private String email;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;

    @Schema(description = "类型", defaultValue = "rebind")
    private String type;


}
