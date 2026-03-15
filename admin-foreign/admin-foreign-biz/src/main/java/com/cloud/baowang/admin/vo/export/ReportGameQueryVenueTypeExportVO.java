package com.cloud.baowang.admin.vo.export;

import cn.hutool.core.util.ObjUtil;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
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
@ExcelIgnoreUnannotated
@I18nClass
@Schema(description = "游戏报表 场馆类型返回vo")
public class ReportGameQueryVenueTypeExportVO {
    @Schema(title = "场馆code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;
    @Schema(title = "场馆code")
    @ExcelProperty("游戏场馆")
    @ColumnWidth(10)
    private String venueCodeText;
    @Schema(title = "币种")
    @ExcelProperty("币种")
    @ColumnWidth(10)
    private String currency;
    @Schema(title = "投注人数")
    @ExcelProperty("投注人数")
    @ColumnWidth(10)
    private Long bettorNum;
    @Schema(title = "注单量")
    @ExcelProperty("注单量")
    @ColumnWidth(10)
    private Long betNum;
    @Schema(title = "投注金额")
    @ExcelProperty("投注金额")
    @ColumnWidth(15)
    private BigDecimal betAmount;
    @Schema(title = "有效投注金额")
    @ExcelProperty("有效投注")
    @ColumnWidth(15)
    private BigDecimal validBetAmount;
    @Schema(title = "输赢金额")
    @ExcelProperty("平台输赢")
    @ColumnWidth(15)
    private BigDecimal winLoseAmount;

    @Schema(title = "打赏金额")
    @ExcelProperty("打赏金额")
    @ColumnWidth(15)
    private BigDecimal tipsAmount;

}
