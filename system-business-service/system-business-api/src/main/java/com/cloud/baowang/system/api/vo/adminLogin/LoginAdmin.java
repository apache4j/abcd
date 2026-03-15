package com.cloud.baowang.system.api.vo.adminLogin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Schema(description = "登录对象")
public class LoginAdmin implements Serializable {


    /**
     * ID
     */
    private String id;

    /**
     * 账号
     */
    @Schema(description =  "用户名")
    private String userName;

    @Schema(description =  "姓名")
    private String nickName;

    @Schema(description =  "userId")
    private String userId;


    /**
     * token
     */
    @Schema(description =  "token")
    private String token;


    @Schema(description =  "最后登录时间")
    private Long lastLoginTime;

    /**
     * Api权限列表
     */
    private List<String> apiPermissions;

    /**
     * Url权限列表
     */
    private List<String> urlList;

    @Schema(description =  "token过期时间")
    private Long expireTime;

    @Schema(description =  "登录时间")
    private Long loginTime;

    @Schema(description = "状态 0 正常 1禁用")
    private Integer status;

    @Schema(description =  "谷歌authKey")
    private String googleAuthKey;

    private Boolean isSuperAdmin;

    /**
     * 数据脱敏 true 需要脱敏 false 不需要脱敏
     */
    private Boolean dataDesensitization;

    /**
     * roleIds
     */
    private List<String> roleIds;

    /**
     * 站点编号
     */
    private String siteCode;

    @Schema(description =  "是否首次登录", hidden = true)
    private Boolean isFirstLogin;

    @Schema(description =  "请求的token", hidden = true)
    private String accessToken;

    @Schema(description =  "角色名称")
    private List<String> roleNames;
}
