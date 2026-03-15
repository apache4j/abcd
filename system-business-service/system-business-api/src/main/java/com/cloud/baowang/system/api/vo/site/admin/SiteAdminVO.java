package com.cloud.baowang.system.api.vo.site.admin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;


/**
 * @author qiqi
 */
@Data
@Schema(description = "职员信息对象")
public class SiteAdminVO implements Serializable {

    @Schema(description = "id")
    private String id;

    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "userId")
    private String userId;

    @Schema(description = "姓名")
    private String nickName;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "谷歌验证秘钥")
    private String googleAuthKey;

    @Schema(description = "最后登录时间")
    private Long lastLoginTime;

    @Schema(description = "状态 0 正常 1禁用")
    private Integer status;

    @Schema(description = "锁定状态")
    private Integer lockStatus;


    @Schema(description = "描述")
    private String remark;


    @Schema(description = "创建时间")
    private Date createdTime;


    @Schema(description = "创建人")
    private String creator;

    private String isSuperAdmin;
    /**
     * Api权限列表
     */
    private List<String> apiPermissions;

    /**
     * URL
     */
    private List<String> urlList;

    /**
     * roleIds
     */
    private List<String> roleIds;

    /**
     * 数据脱敏 true 需要脱敏 false 不需要脱敏
     */
    private Boolean dataDesensitization= false;

    @Schema(description = "是否已重置google 1 是 0 否")
    private Integer isSetGoogle;

    /**
     * ip白名单， 逗号隔开
     */
    private String allowIps;


    @Schema(description =  "角色名称")
    private List<String> roleNames;

}
