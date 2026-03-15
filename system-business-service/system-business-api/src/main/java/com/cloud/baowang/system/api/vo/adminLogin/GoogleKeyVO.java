package com.cloud.baowang.system.api.vo.adminLogin;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class GoogleKeyVO {

    private String siteCode;

    private String userName;

    //谷歌验证秘钥
    private String googleAuthKey;
}
