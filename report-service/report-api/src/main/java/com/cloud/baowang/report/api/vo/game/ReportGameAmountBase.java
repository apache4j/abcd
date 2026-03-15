package com.cloud.baowang.report.api.vo.game;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "游戏报表 金额基类")
@I18nClass
public class ReportGameAmountBase {

    private String currency;
    @Schema(title = "投注人数")
    private Long bettorNum;
    @Schema(title = "注单量")
    private Long betNum;
    @Schema(title = "投注金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal betAmount;
    @Schema(title = "有效投注金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal validBetAmount;
    @Schema(title = "输赢金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal winLoseAmount;
    @Schema(title = "投注金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal betWinLose;
    @Schema(title = "打赏金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal tipsAmount;
    @Schema(title = "全部汇总;true=汇总,false=分页")
    private Boolean allSumType;

    @Schema(title = "分页汇总:true")
    private Boolean pageSumType;

}
