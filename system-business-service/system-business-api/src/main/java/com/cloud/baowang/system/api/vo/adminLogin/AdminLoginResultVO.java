package com.cloud.baowang.system.api.vo.adminLogin;

import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 后台-登陆返回对象
 *
 * @author qiqi
 */
@Data
@Schema(description = "后台-登陆返回对象")
public class AdminLoginResultVO {

    /**
     * ID
     */
    private String id;

    /**
     * 用户名
     */
    @Schema(description =  "用户名")
    private String userName;
    @Schema(description =  "用户ID")
    private String userId;

    /**
     * 姓名
     */
    private String nickName;

    @Schema(description = "站点模式 0 全包 1 包风控 2 包财务 3 不包")
    private Integer siteModel;

    /**
     * token
     */
    @Schema(description =  "token")
    private String token;


    @Schema(description =  "最后登录时间")
    private Long lastLoginTime;

    @Schema(description =  "token过期时间")
    private Long expireTime;

    /**
     * google验证--key
     */
    @Schema(description =  "google验证--key")
    private String googleAuthKey;

    @Schema(description =  "是否是需要google扫码  true 需要  false 不需要")
   private boolean needGoogle;

    @Schema(description =  "数据脱敏 true 需要脱敏 false 不需要脱敏")
    private Boolean dataDesensitization;


    @Schema(description =  "角色名称")
    private String roleNames;

    @Schema(description =  "角色")
    private String roleIds;
}
