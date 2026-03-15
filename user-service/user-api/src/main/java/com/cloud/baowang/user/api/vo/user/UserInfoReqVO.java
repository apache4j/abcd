package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "会员信息返回实体")
public class UserInfoReqVO {

    @Schema(description = "站点code")
    private String siteCode;

    @Schema(description = "是否非一级VIP")
    private boolean vipNotGrade1Flag = false;

    @Schema(description = "出生日期")
    private String birthday;

    @Schema(description = "出生日期右")
    private String birthdayRight;

    @Schema(description = "出生日期左")
    private String birthdayLeft;


    @Schema(description = "查询条数")
    private long limitCount = 5000;

    @Schema(description = "maxId")
    private String minId;


    @Schema(description = "注册日期开始")
    private Long registerTimeStart;

    @Schema(description = "注册日期结束")
    private String registerTimeEnd;
}
