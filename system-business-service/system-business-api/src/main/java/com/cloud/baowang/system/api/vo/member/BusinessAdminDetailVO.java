package com.cloud.baowang.system.api.vo.member;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author qiqi
 */
@Data
@Schema(description = "职员详情返回对象")
public class BusinessAdminDetailVO {

    @Schema(description = "ID")
    private String id;

    @Schema(description = "管理员ID", required = true)
    private String userId;

    @Schema(description = "用户名", required = true)
    private String userName;

    @Schema(description = "姓名", required = true)
    private String nickName;

    @Schema(description = "谷歌验证秘钥")
    private String googleAuthKey;

    @Schema(description = "角色IDS")
    private String[] roleIds;

}
