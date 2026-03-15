package com.cloud.baowang.activity.api.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Schema(description = "充值活动详情信息")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ActivityDepositDetailVO {

    @Schema(description = "存款总金额", required = true)
    private BigDecimal depositAmount;

    @Schema(description = "存款总金额-货币类型", required = true)
    private String depositCurrencyCode;

    @Schema(description = "活动彩金总金额", required = true)
    private BigDecimal activityAmount;

    @Schema(description = "活动彩金总金额-货币类型", required = true)
    private String activityAmountCurrencyCode;

    @Schema(description = "需打流水")
    private BigDecimal runningWater;

    @Schema(description = "需打流水-货币类型")
    private String runningWaterCurrencyCode;

//    @Schema(description = "币种代码")
//    private String currencyCode;

    @Schema(description = "详情是否满足条件 ")
    private boolean applyButtonEnabled;

    @Schema(description = "参与资格:true=可以参与,false=不可以参与")
    private Boolean activityCondition;
}
