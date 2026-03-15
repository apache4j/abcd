package com.cloud.baowang.activity.api.vo.redbag;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Schema(description = "红包雨中奖名单-c端")
@AllArgsConstructor
@NoArgsConstructor
public class RedBagWinnerVO {
    @Schema(description = "会员id")
    private String userId;
    @Schema(description = "会员账号")
    private String userAccount;
    @Schema(description = "红包金额")
    private BigDecimal redBagAmount;
    @Schema(description = "中奖时间 时间戳")
    private Long hitTime;

}
