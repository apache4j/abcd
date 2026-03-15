package com.cloud.baowang.site.vo.export.gamereport;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "游戏报表 场馆查询返回vo")
public class ReportGameQueryVenueExportVO {
    @Schema(title = "三方游戏类型")
    private String thirdGameType;

    @Schema(title = "游戏名称")
    @ExcelProperty("游戏名称")
    @ColumnWidth(15)
    private String gameName;

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
    @ExcelProperty("有效投注金额")
    @ColumnWidth(15)
    private BigDecimal validBetAmount;


    @Schema(title = "投注输赢")
    @ExcelProperty("投注输赢")
    @ColumnWidth(15)
    private BigDecimal betWinLose;

    @Schema(title = "打赏金额")
    @ExcelProperty("打赏金额")
    @ColumnWidth(15)
    private BigDecimal tipsAmount;

    @Schema(title = "平台输赢")
    @ExcelProperty("平台输赢")
    @ColumnWidth(15)
    private BigDecimal winLoseAmount;



}
