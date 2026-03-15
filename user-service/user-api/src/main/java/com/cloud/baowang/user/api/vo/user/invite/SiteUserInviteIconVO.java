package com.cloud.baowang.user.api.vo.user.invite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: fangfei
 * @createTime: 2024/11/25 16:57
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "邀请码配置图标VO")
public class SiteUserInviteIconVO {
    private String configId;
    @Schema(description = "语言code")
    private String language;
    @Schema(description = "语言名称")
    private String languageName;
    @Schema(description = "展示code")
    private String showCode;
    @Schema(title = "icon完整地址")
    private String messageFileUrl;
    @Schema(description = "展示iconUrl")
    private String iconUrl;
}
