package com.cloud.baowang.report.api.vo.game;

import com.cloud.baowang.common.core.constants.CommonConstant;
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
@Schema(description = "游戏报表 场馆查询返回vo")
public class ReportGameQueryVenueVO extends ReportGameAmountBase{
    @Schema(title = "三方游戏类型")
    private String thirdGameType;
    @Schema(title = "游戏名称")
    private String gameName;

    @Schema(title = "场馆")
    private String venueCode;

//    @Schema(title = "币种")
//    private String currency;
    @Schema(title = "平台币code")
    private String platCurrency = CommonConstant.PLAT_CURRENCY_CODE;
//    @Schema(title = "投注人数")
//    private Long bettorNum;
//    @Schema(title = "注单量")
//    private Long betNum;
//    @Schema(title = "投注金额")
//    @JsonSerialize(using = BigDecimalJsonSerializer.class)
//    private BigDecimal betAmount;
//    @Schema(title = "有效投注金额")
//    @JsonSerialize(using = BigDecimalJsonSerializer.class)
//    private BigDecimal validBetAmount;
//    @Schema(title = "输赢金额")
//    @JsonSerialize(using = BigDecimalJsonSerializer.class)
//    private BigDecimal winLoseAmount;
//    @Schema(title = "投注金额")
//    @JsonSerialize(using = BigDecimalJsonSerializer.class)
//    private BigDecimal betWinLose;
//    @Schema(title = "打赏金额")
//    @JsonSerialize(using = BigDecimalJsonSerializer.class)
//    private BigDecimal tipsAmount;

}
