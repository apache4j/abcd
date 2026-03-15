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
@Schema(title = "会员头像添加排序vo")
@I18nClass
public class SiteUserAvatarConfigAddSortVO {

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

    @Schema(description = "启用/禁用状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer status;

    private String statusText;

    @Schema(description = "排序")
    private Integer sort;

    @Schema(description = "操作人", hidden = true)
    private String updater;

    @Schema(description = "修改时间",hidden = true)
    private Long updatedTime;


}
