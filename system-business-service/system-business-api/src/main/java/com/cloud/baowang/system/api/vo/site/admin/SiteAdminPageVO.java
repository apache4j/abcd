package com.cloud.baowang.system.api.vo.site.admin;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


/**
 * @author qiqi
 */
@Data
@Schema(description = "职员列表分页返回对象")
@I18nClass
public class SiteAdminPageVO implements Serializable {
    @Schema(description = "ID")
    private String id;

    @Schema(description = "站点编号")
    private String siteCode;

    @Schema(description = "管理员ID")
    private String userId;

    @Schema(description = "用户名")
    private String userName;

    @Schema(description = "姓名")
    private String nickName;


    @Schema(description = "谷歌验证秘钥")
    private String googleAuthKey;

    @Schema(description = "最后登录时间")
    private Long lastLoginTime;

    @Schema(description = "状态 1 正常 0禁用")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS )
    private Integer status;

    @Schema(description = "状态名称")
    private String statusText;


    @Schema(description = "描述")
    private String remark;

    @Schema(description = "ip白名单")
    private String allowIps;


    @Schema(description = "创建时间")
    private Long createdTime;

    private String creator;

    @Schema(description = "创建人")
    private String creatorName;

    @Schema(description = "锁定状态 0 未锁定 1 已锁定")
    private Integer lockStatus;

}
