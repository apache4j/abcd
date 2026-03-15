package com.cloud.baowang.user.api.vo.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @className: UserEditVipVO
 * @author: wade
 * @description: vip等级更改入参
 * @date: 2/9/24 13:28
 */
@Schema(description = "vip等级更改入参")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEditVipVO {
    @Schema(description = "userId")
    private String userId;

    @Schema(description = "vip当前等级")
    private Integer vipGradeCode;

    @Schema(description = "vip升级后的等级")
    private Integer vipGradeUp;

    @Schema(description = "vip段位")
    private Integer vipRank;
}
