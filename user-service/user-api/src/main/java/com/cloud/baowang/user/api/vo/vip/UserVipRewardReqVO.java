package com.cloud.baowang.user.api.vo.vip;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户vip领取入参")
public class UserVipRewardReqVO implements Serializable {
    @Schema(description = "账号", hidden = true)
    private String userAccount;

    @Schema(description = "userID", hidden = true)
    private String userId;

    @Schema(description = "领奖订单号")
    private String id;

    @Schema(description = "站点code", hidden = true)
    private String siteCode;


}
