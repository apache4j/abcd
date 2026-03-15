package com.cloud.baowang.common.excel.freeGame;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class AddFreeGameReadExcelDTO {
    @ExcelProperty(index = 0)
    private String userAccount;

    @ExcelProperty(index = 1) // Excel 第 2 列的表头
    private Integer acquireNum;

    @ExcelProperty(index = 2) // Excel 第 2 列的表头
    @Schema(title = "限注金额")
    private BigDecimal betLimitAmount;

    @ExcelProperty(index = 3) // Excel 第 2 列的表头
    @Schema(title = "币种")
    private String currency;
}
