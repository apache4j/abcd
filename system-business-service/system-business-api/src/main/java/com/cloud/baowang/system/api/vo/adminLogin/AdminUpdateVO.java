package com.cloud.baowang.system.api.vo.adminLogin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
@Schema(description = "站点后台更新对象")
public class AdminUpdateVO {
    @Schema(description = "管理员id")
    private String id;

    @Schema(description = "站点编号")
    @NotEmpty(message = "站点编号不能为空")
    private String siteCode;

    @Schema(description = "用户名")
    @NotEmpty(message = "用户名不能为空")
    private String userName;

    @Schema(description = "谷歌验证秘钥")
    private String googleAuthKey;

    @Schema(description = "密码")
    private String password;

    @Schema(description =  "是否已重置google 1 是 0 否", hidden = true)
    private Integer isSetGoogle;

    @Schema(description = "最后登录时间")
    private Long lastLoginTime;

}
