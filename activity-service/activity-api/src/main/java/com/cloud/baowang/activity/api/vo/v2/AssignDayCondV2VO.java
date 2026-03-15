package com.cloud.baowang.activity.api.vo.v2;

import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Schema(description = "指定存款日期匹配条件 百分比时传递 minDepositAmt,maxDepositAmt,acquireNum,acquireAmount")
@Data
public class AssignDayCondV2VO implements Serializable {

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


    @Schema(description = "优惠百分比")
    private BigDecimal depositPercent;

    @Schema(description = "赠送金额最大值")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal acquireAmountMax;

    //洗码倍率
    private BigDecimal washRatio;

    // 所需流水
    private BigDecimal requiredTurnover;


    @Schema(description = "场馆类型")
    //@NotNull(message = "场馆不能为空,固定PP")
    private String venueCode;
    @Schema(description = "pp游戏code")
    //@NotNull(message = "pp游戏code不能为空")
    private String accessParameters;
    @Schema(description = "限注金额")
    //@NotNull(message = "限注金额不能为空")
    private BigDecimal betLimitAmount;


    @Schema(description = "币种")
    private String currencyCode;

}
