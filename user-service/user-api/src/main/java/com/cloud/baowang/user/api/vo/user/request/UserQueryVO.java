package com.cloud.baowang.user.api.vo.user.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author: fangfei
 * @createTime: 2024/08/01 23:38
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "会员查询对象")
public class UserQueryVO implements Serializable {
    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "手机号码")
    private String phone;
}
