package com.cloud.baowang.wallet.api.vo.recharge;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(title = "阿里实名认证请求对象")
public class AliAuthRequestVO {
    @Schema(description = "userId", hidden = true)
    private String userId;

    @Schema(description = "手机区号")
    @NotNull(message = "手机区号不能为空!")
    private String areaCode;

    @Schema(description = "手机号码")
    @NotNull(message = "手机号码不能为空!")
    private String phone;

    @Schema(description = "姓名")
    @NotNull(message = "姓名不能为空!")
    private String userName;

    @Schema(description = "出生年月")
    @NotNull(message = "出生年月不能为空!")
    private String birthday;

    @Schema(title = "验证码")
    @NotEmpty(message = "验证码不能为空")
    private String verifyCode;
}
