package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "获取手机验证码请求对象")
public class UserDetailQueryVO implements Serializable {
    @Schema(description = "手机号")
    private String phone;
    @Schema(description = "会员账号")
    private String userAccount;

    /*@Schema(description = "会员注册信息")
    private String userRegister;*/

    @Schema(description = "站点code",hidden = true)
    private String siteCode;

}
