package com.cloud.baowang.user.api.vo.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "用户更新请求对象")
public class UserEditVO implements Serializable {
    @Schema(description = "用户id")
    private String userAccount;

    @Schema(description = "区号")
    private String areaCode;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "手机号码验证状态 0 未验证  1已验证")
    private Integer phoneStatus;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "邮箱验证状态 0 未验证  1已验证")
    private Integer mailStatus;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "昵称")
    private String nickName;

    @Schema(description = "用户头像code")
    private String avatarCode;

    @Schema(description = "用户头像")
    private String avatar;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;

    @Schema(description = "取款密码")
    private String withdrawPwd;
}
