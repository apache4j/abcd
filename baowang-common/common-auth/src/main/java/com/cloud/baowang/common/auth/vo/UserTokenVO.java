package com.cloud.baowang.common.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author: fangfei
 * @createTime: 2024/08/11 22:36
 * @description:
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "会员token解析返回对象")
public class UserTokenVO {
    @Schema(title = "会员UserId")
    private String userId;
    @Schema(title = "会员账号")
    private String userAccount;
    @Schema(title = "站点code")
    private String siteCode;
}
