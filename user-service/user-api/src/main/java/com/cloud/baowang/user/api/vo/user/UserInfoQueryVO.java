package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 会员基本信息VO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "账号快速信息")
public class UserInfoQueryVO implements Serializable {

    @Schema(description = "会员注册信息")
    private String userAccount;

    /*@Schema(description = "注册信息")
    private String userRegister;*/
}
