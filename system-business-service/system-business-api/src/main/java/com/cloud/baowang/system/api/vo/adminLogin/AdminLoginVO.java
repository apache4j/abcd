package com.cloud.baowang.system.api.vo.adminLogin;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 后台管理系统登录请求对象
 *
 * @author qiqi
 */
@Data
@Schema(description = "AUTH登录请求对象")
public class AdminLoginVO {

    @Schema(description =  "用戶账号")
    private String userName;

    @Schema(description =  "userId")
    private String userId;

    @Schema(description =  "用戶密码")
    private String password;

    @Schema(description =  "校验码")
    private String verifyCode;

    @Schema(description =  "登录IP")
    private String loginIp;

    @Schema(description =  "登录地点")
    private String loginLocation;

    @Schema(description =  "终端设备号")
    private String deviceCode;

    @Schema(description =  "站点code", hidden = true)
    private String siteCode;

    @Schema(description =  "请求域名", hidden = true)
    private String domain;


}
