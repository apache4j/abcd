package com.cloud.baowang.activity.vo.mq;

import com.cloud.baowang.common.core.annotations.I18nClass;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Schema(description = "一天用户打码的金额")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayVenueBetAmountVO implements Serializable {

    //法币 投注金额
    private BigDecimal betAmount;

    //法币 有效投注
    private BigDecimal validAmount;

    //投注盈亏
    private BigDecimal winLossAmount;

    //用户币种
    private String currencyCode;


}
