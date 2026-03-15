package com.cloud.baowang.system.api.vo.site.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.Length;


/**
 * @author qiqi
 */
@Data
@Schema(description = "职员添加请求对象")
public class SiteAdminAddVO {


    @Schema(description = "用户名", required = true)
    @NotEmpty(message = "用户名不能为空")
    @Length(min = 6, max = 12, message = "用户名介于6-12个字符!")
    private String userName;

    @Schema(description = "姓名", required = true)
    @NotEmpty(message = "姓名不能为空")
    @Length(min = 2, max = 10, message = "姓名介于2-10个字符!")
    private String nickName;

    @Schema(description = "密码", required = true)
    @NotEmpty(message = "密码不能为空")
    @Length(min = 8, max = 16, message = "密码介于8-16个字符!")
    private String password;

    @Schema(description = "确认密码", required = true)
    @NotEmpty(message = "确认密码不能为空")
    @Length(min = 8, max = 16, message = "确认密码介于8-16个字符!")
    private String confirmPassword;

    @Schema(description = "谷歌验证秘钥", required = false)
    private String googleAuthKey;

    @Schema(description = "角色IDS")
    @NotNull(message = "角色IDS不能为空")
    @Size(min = 1, message = "至少选择一个角色")
    private String[] roleIds;

    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(description = "IP白名单")
    @NotEmpty(message = "IP白名单不能为空")
    @Length(min = 1, max = 300, message = "IP白名单最大长度为300个字符!")
    private String allowIps;

    private String isSuperAdmin;

    private String creator;


}
