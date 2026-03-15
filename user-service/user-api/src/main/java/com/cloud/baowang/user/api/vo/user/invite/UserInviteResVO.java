package com.cloud.baowang.user.api.vo.user.invite;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/23 22:03
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "好友邀请信息")
public class UserInviteResVO implements Serializable {
    @Schema(description = "siteCode", hidden = true)
    private String siteCode;
    @Schema(description = "背景图片地址")
    private String iconUrl;
    @Schema(description = "邀请域名")
    private String inviteUrl;
    @Schema(description = "邀请码")
    private String inviteCode;
}
