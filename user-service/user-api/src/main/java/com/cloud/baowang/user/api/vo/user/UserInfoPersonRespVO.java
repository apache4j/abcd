package com.cloud.baowang.user.api.vo.user;


import cn.hutool.core.util.ObjectUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "会员个人资料 reponse")
public class UserInfoPersonRespVO {

    @Schema(description = "注册时间")
    private long registerTime;

    @Schema(description = "会员ID")
    private String userId;

    @Schema(description = "会员账号")
    private String userAccount;

    @Schema(description = "用户姓名")
    private String userName;

    @Schema(description = "性别 1-男 2-女")
    private Integer gender;


    @Schema(description = "出生日期")
    private String birthday;

    @Schema(description = "区号")
    private String areaCode;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "是否需要实名认证")
    private Boolean realNameAuth;


}
