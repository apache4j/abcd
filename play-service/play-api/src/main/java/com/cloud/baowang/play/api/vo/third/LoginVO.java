package com.cloud.baowang.play.api.vo.third;

import com.cloud.baowang.common.core.constants.ConstantsCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "三方游戏登录对象")
public class LoginVO implements Serializable {

    @Schema(title = "场馆code", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = ConstantsCode.PARAM_ERROR)
    private String venueCode;

    @Schema(title = "游戏code")
    private String gameCode;

    @Schema(title = "用户账号", hidden = true)
    private String userAccount;

    @Schema(title = "客户端ip", hidden = true)
    private String ip;

    @Schema(title = "用户id", hidden = true)
    private String userId;

    @Schema(title = "站点code", hidden = true)
    private String siteCode;

    @Schema(title = "币种", hidden = true)
    private String currencyCode;

    @Schema(title = "语言CODE", hidden = true)
    private String languageCode;

    @Schema(title = "设备：PC H5", hidden = true)
    private String device;
}
