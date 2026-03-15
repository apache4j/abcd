package com.cloud.baowang.report.api.vo.game;

import cn.hutool.core.util.ObjUtil;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
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
@Schema(description = "游戏报表站点返回vo")
@I18nClass
public class ReportGameQuerySiteVO extends ReportGameAmountBase{
    @Schema(title = "游戏类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_TYPE)
    private Integer venueType;
    @Schema(title = "游戏类型-文本")
    private String venueTypeText;
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
//    @Schema(title = "打赏金额")
//    private BigDecimal tipsAmount;
//    @Schema(title = "投注金额")
//    private BigDecimal betWinLose;
//
//    @Schema(title = "是否汇总;true=汇总,false=分页")
//    private Boolean allSumType;

}
