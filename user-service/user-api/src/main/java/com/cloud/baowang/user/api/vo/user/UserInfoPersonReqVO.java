package com.cloud.baowang.user.api.vo.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会员个人资料 request")
public class UserInfoPersonReqVO {


    @Schema(description = "会员ID", hidden = true)
    private String userId;

    @Schema(description = "会员账号", hidden = true)
    private String userAccount;

    @Schema(description = "用户姓名")
    private String userName;

    @Schema(description = "性别 1-男 2-女")
    private Integer gender;


    @Schema(description = "出生日期")
    private String birthday;


}
