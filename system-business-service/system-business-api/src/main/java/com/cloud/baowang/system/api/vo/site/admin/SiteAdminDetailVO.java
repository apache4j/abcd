package com.cloud.baowang.system.api.vo.site.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(description = "职员详情返回对象")
public class SiteAdminDetailVO {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "用户ID")
    private String userId;

    @Schema(description = "用户名", required = true)
    private String userName;

    @Schema(description = "姓名", required = true)
    private String nickName;

    @Schema(description = "谷歌验证秘钥")
    private String googleAuthKey;

    @Schema(description = "角色IDS")
    private String[] roleIds;

    @Schema(description = "ip白名单")
    private String allowIps;

}
