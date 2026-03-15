package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(title = "更新会员 Request")
public class UserInfoEditVO {

    private String id;

    @Schema(title = "会员账号")
    private String userAccount;

    @Schema(title = "密码")
    private String password;

    @Schema(title = "风控层级id")
    private String riskLevelId;

    @Schema(title = "最后登录时间")
    private Long lastLoginTime;

    @Schema(title = "最后登录IP")
    private String lastLoginIp;

    @Schema(title = "最后登录设备号")
    private String lastDeviceNo;

    @Schema(title = "手机号码")
    private String phone;

    @Schema(title = "邮箱")
    private String email;

    @Schema(title = "离线天数")
    private Integer offlineDays;

    private Long updatedTime;

    @Schema(description = "IP归属地")
    private String ipAddress;

}
