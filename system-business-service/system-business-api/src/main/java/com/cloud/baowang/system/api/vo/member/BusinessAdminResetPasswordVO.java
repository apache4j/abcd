package com.cloud.baowang.system.api.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


/**
 * @author qiqi
 */
@Data
@Schema(description = "职员重置密码请求对象")
public class BusinessAdminResetPasswordVO {

    @Schema(description = "职员ID", required = true)
    @NotNull(message = "职员ID不能为空")
    private String id;

    @Schema(description = "密码", required = true)
    @NotEmpty(message = "密码不能为空")
    @Length(min = 8, max = 16, message = "密码介于8-16个字符!")
    private String password;

    @Schema(description = "确认密码", required = true)
    @NotEmpty(message = "确认密码不能为空")
    @Length(min = 8, max = 16, message = "确认密码介于8-16个字符!")
    private String confirmPassword;

    @Schema(hidden = false)
    private String siteCode;


}
