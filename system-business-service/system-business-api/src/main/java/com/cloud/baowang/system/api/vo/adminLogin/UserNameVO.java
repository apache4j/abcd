package com.cloud.baowang.system.api.vo.adminLogin;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "只有 userName")
public class UserNameVO {

    private String siteCode;

    private String userName;

}
