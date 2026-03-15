package com.cloud.baowang.user.api.vo.userAvatar;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.PageVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "会员头像列表查询对象")
@I18nClass
public class SiteUserAvatarConfigPageQueryVO extends PageVO {
    /**
     * 站点代码
     */
    @Schema(description = "站点代码", hidden = true)
    private String siteCode;

    @Schema(description = "头像id")
    private String avatarId;

    /**
     * 头像名称
     */
    @Schema(description = "头像名称")
    private String avatarName;

    @Schema(description = "操作人")
    private String updater;
    @Schema(description = "启用/禁用状态")
    private Integer status;
}
