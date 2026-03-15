package com.cloud.baowang.user.api.vo.user;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Author 小智
 * @Date 11/5/23 6:27 PM
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户个人资料")
public class UserPersonalVO implements Serializable {

    @Schema(description = "姓名")
    private String userName;

    @Schema(description = "性别")
    private Integer gender;

    @Schema(description = "性别名称")
    private String genderName;

    @Schema(description = "出生日期")
    private String birthday;

    @Schema(description = "区号")
    private String areaCode;

    @Schema(description = "手机号码")
    private String phone;

    @Schema(description = "邮箱")
    private String email;

}
