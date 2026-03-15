package com.cloud.baowang.report.api.vo.venuewinlose;

import cn.hutool.core.util.ObjUtil;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
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
import java.math.RoundingMode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "场馆盈亏报表场馆详情分页返回参数")
@I18nClass
@ExcelIgnoreUnannotated
public class VenueWinLossDetailResVO {
    @Schema(description = "场馆code")
//    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;
    @Schema(description = "场馆文本")
    @ExcelProperty("场馆")
    @ColumnWidth(15)
    private String venueCodeText;
    @Schema(description = "币种")
    @ExcelProperty("主币种")
    @ColumnWidth(15)
    private String currency;
    @Schema(description = "平台币币种")
    private String platCurrency;
    @Schema(description = "投注人数")
    @ExcelProperty("投注人数")
    @ColumnWidth(15)
    private Integer bettorCount;
    @Schema(description = "注单量")
    @ExcelProperty("注单量")
    @ColumnWidth(15)
    private Integer betCount;
    @Schema(description = "投注金额")
    @ExcelProperty("投注金额")
    @ColumnWidth(15)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal betAmount;
    @Schema(description = "有效投注")
    @ExcelProperty("有效投注")
    @ColumnWidth(15)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal validBetAmount;
    @ExcelProperty("平台投注输赢")
    @ColumnWidth(15)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal userWinlossAmount;
    @Schema(description = "打赏金额")
    @ExcelProperty("打赏金额")
    @ColumnWidth(15)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal tipsAmount;
    @Schema(description = "平台游戏输赢")
    @ExcelProperty("平台游戏输赢")
    @ColumnWidth(15)
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal winlossAmount;




//    public BigDecimal getBetAmount() {
//        if (ObjUtil.isNotNull(betAmount)) {
//            betAmount = betAmount.setScale(CommonConstant.business_two, RoundingMode.HALF_DOWN);
//        }
//        return betAmount;
//    }
//
//    public BigDecimal getValidBetAmount() {
//        if (ObjUtil.isNotNull(validBetAmount)) {
//            validBetAmount = validBetAmount.setScale(CommonConstant.business_two, RoundingMode.HALF_DOWN);
//        }
//        return validBetAmount;
//    }
//
//    public BigDecimal getWinlossAmount() {
//        if (ObjUtil.isNotNull(winlossAmount)) {
//            winlossAmount = winlossAmount.setScale(CommonConstant.business_two, RoundingMode.HALF_DOWN);
//        }
//        return winlossAmount;
//    }
}
