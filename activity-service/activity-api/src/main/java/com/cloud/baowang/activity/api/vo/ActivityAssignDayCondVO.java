package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "指定存款日期匹配条件 百分比时传递 minDepositAmt,maxDepositAmt,acquireNum,acquireAmount")
@Data
public class ActivityAssignDayCondVO implements Serializable {

    //==========固定金额时传递==============
    @Schema(description = "累计存款最小金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal minDepositAmt;

    @Schema(description = "累计存款最大金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal maxDepositAmt;


    @Schema(description = "赠送免费旋转次数")
    private Integer acquireNum;

    @Schema(description = "赠送金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal acquireAmount;


    //==========百分比时传递 minDepositAmt,depositPercent,acquireNum,acquireAmountMax==============
    @Schema(description = "优惠百分比")
    private BigDecimal depositPercent;

    @Schema(description = "赠送金额最大值")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal acquireAmountMax;

    //洗码倍率
    private BigDecimal washRatio;

    // 所需流水
    private BigDecimal requiredTurnover;

    @Schema(description = "币种")
    private String currencyCode;
}
