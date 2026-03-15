package com.cloud.baowang.user.api.vo.medal;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/8/6 11:23
 * @Version: V1.0
 **/
@Data
@Schema(description = "宝箱奖励参数")
public class MedalRewardAcquireReqVO {
    /**
     * 站点代码
     */
    @Schema(description = "站点代码",hidden = true)
    private String siteCode;

    @Schema(description = "宝箱编号")
    @NotNull(message = "宝箱编号不能为空!")
    private Integer rewardNo;
    /**
     * 用户编号
     */
    @Schema(description = "用户账号",hidden = true)
    private String userAccount;

    /**
     * 会员ID
     */
    @Schema(description = "会员ID",hidden = true)
    private String userId;



}
