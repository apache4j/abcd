package com.cloud.baowang.report.api.vo.game;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "游戏报表 场馆类型返回vo")
@I18nClass
public class ReportGameQueryVenueTypeVO extends ReportGameAmountBase{
    @Schema(description = "三方场馆code")
    private String venueCode;
    @Schema(description = "三方场馆-文本")
    private String venueCodeText;
    @Schema(title = "币种")
    private String currency;
    @Schema(title = "平台币code")
    private String platCurrency = CommonConstant.PLAT_CURRENCY_CODE;
//    @Schema(title = "投注人数")
//    private Long bettorNum;
//    @Schema(title = "注单量")
//    private Long betNum;
//    @Schema(title = "投注金额")
//    private BigDecimal betAmount;
//    @Schema(title = "有效投注金额")
//    private BigDecimal validBetAmount;
//    @Schema(title = "输赢金额")
//    private BigDecimal winLoseAmount;
//    @Schema(title = "投注金额")
//    private BigDecimal betWinLose;
//    @Schema(title = "报表")
//    private BigDecimal tipsAmount;


}
