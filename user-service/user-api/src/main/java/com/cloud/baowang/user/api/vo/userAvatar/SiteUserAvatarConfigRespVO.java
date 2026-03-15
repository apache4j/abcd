package com.cloud.baowang.user.api.vo.userAvatar;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "会员头像配置vo")
@I18nClass
public class SiteUserAvatarConfigRespVO {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "头像id")
    private String avatarId;
    /**
     * 站点代码
     */
    @Schema(description = "站点代码")
    private String siteCode;
    /**
     * 头像名称
     */
    @Schema(description = "头像名称")
    private String avatarName;
    /**
     * 头像图片地址
     */
    @Schema(description = "头像图片地址")
    @I18nField(type = I18nFieldTypeConstants.FILE)
    private String avatarImageUrl;

    @Schema(description = "图片完整地址-只做展示用")
    private String avatarImageUrlFileUrl;

    @Schema(description = "启用/禁用状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;

    @Schema(description = "启用/禁用状态")
    private String statusText;

    @Schema(description = "有无被使用0.否,1.是(决定是否禁用删除,禁用状态)")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.YES_NO)
    private Integer isUsed;

    @Schema(description = "有无被使用")
    private String isUsedText;

    @Schema(description = "创建人")
    private String creator;
    @Schema(description = "创建时间")
    private Long createdTime;

    @Schema(description = "修改人")
    private String updater;
    @Schema(description = "修改时间")
    private String updatedTime;

    private Integer sort;

}
