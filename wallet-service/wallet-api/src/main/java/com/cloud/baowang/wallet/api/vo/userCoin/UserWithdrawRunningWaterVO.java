package com.cloud.baowang.wallet.api.vo.userCoin;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "用户提现流水信息")
public class UserWithdrawRunningWaterVO {

    @Schema(description = "用户余额")
    private BigDecimal userBalance;

    @Schema(description = "所需流水")
    private BigDecimal needRunningWater ;

    @Schema(description = "已完成投注流水")
    private BigDecimal completedRunningWater;

    @Schema(description = "剩余流水")
    private BigDecimal remainingRunningWater;

    @Schema(description = "流水开始统计时间")
    private Long runningWaterStartTime;

    @Schema(description = "主货币币种")
    private String currency;
    @Schema(description = "参与存款活动限制流水")
    private BigDecimal activityRunningWater;

}
