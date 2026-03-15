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
@Schema(description = "邀请码图标")
public class InviteIconVO {
    @Schema(description = "语言")
    private String language;
    @Schema(description = "PC端图标")
    private String iconUrl;
}
