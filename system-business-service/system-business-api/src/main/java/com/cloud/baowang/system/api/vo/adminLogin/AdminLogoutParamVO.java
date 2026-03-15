package com.cloud.baowang.system.api.vo.adminLogin;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "踢出职员请求对象")
public class AdminLogoutParamVO {

    @Schema(description =  "职员名称")
    private String userName;

    @Schema(description =  "职员ID")
    private String userId;

    private String siteCode;

}
