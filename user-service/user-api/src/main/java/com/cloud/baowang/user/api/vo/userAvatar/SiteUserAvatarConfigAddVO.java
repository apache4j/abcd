package com.cloud.baowang.user.api.vo.userAvatar;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(title = "会员头像配置vo")
@I18nClass
public class SiteUserAvatarConfigAddVO {

    @Schema(description = "主键")
    private String id;
    /**
     * 站点代码
     */
    @Schema(description = "站点代码", hidden = true)
    private String siteCode;
    /**
     * 头像名称
     */
    @Schema(description = "头像名称")
    @NotBlank(message = ConstantsCode.AVATAR_NAME_NOT_EXIT)
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
    private Integer status;

    @Schema(description = "创建人", hidden = true)
    private String creator;
    @Schema(description = "创建时间", hidden = true)
    private Long createdTime;

    @Schema(description = "修改人", hidden = true)
    private String updater;
    @Schema(description = "修改时间", hidden = true)
    private Long updatedTime;


}
