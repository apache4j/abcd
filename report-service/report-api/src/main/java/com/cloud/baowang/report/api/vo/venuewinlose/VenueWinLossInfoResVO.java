package com.cloud.baowang.report.api.vo.venuewinlose;

import cn.hutool.core.util.ObjUtil;
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
@Schema(description = "场馆盈亏报表详情返回参数")
@I18nClass
public class VenueWinLossInfoResVO {
    @Schema(description = "日期时间戳")
    private Long dayMillis;
    @Schema(description = "场馆code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;
    @Schema(description = "场馆文本")
    private String venueCodeText;
    @Schema(description = "币种")
    private String currency;
    @Schema(description = "平台币币种")
    private String platCurrency = CommonConstant.PLAT_CURRENCY_CODE;
    @Schema(description = "投注人数")
    private Integer bettorCount;
    @Schema(description = "注单量")
    private Integer betCount;
    @Schema(description = "投注金额")
    private BigDecimal betAmount;
    @Schema(description = "有效投注")
    private BigDecimal validBetAmount;
    @Schema(description = "会员输赢")
    private BigDecimal winlossAmount;
    @Schema(description = "打赏金额")
    private BigDecimal tipsAmount;
    @Schema(description = "平台投注输赢")
    private BigDecimal userWinlossAmount;


    public BigDecimal getBetAmount() {
        if (ObjUtil.isNotNull(betAmount)) {
            betAmount = betAmount.setScale(CommonConstant.business_two, RoundingMode.HALF_DOWN);
        }
        return betAmount;
    }

    public BigDecimal getValidBetAmount() {
        if (ObjUtil.isNotNull(validBetAmount)) {
            validBetAmount = validBetAmount.setScale(CommonConstant.business_two, RoundingMode.HALF_DOWN);
        }
        return validBetAmount;
    }

    public BigDecimal getWinlossAmount() {
        if (ObjUtil.isNotNull(winlossAmount)) {
            winlossAmount = winlossAmount.setScale(CommonConstant.business_two, RoundingMode.HALF_DOWN);
        }
        return winlossAmount;
    }

    public BigDecimal getTipsAmount() {
        if (ObjUtil.isNotNull(tipsAmount)) {
            tipsAmount = tipsAmount.setScale(CommonConstant.business_two, RoundingMode.HALF_DOWN);
        }
        return tipsAmount;
    }
}
