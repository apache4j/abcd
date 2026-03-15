package com.cloud.baowang.site.vo.export.report;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;


@Data
@ExcelIgnoreUnannotated
@I18nClass
@Schema(title = "会员存取款日报表导出对象")
public class ReportUserDepositWithdrawExportVO {




    /**
     * 日期
     */
    @Schema(description ="日期")
    private Long day;

    @ExcelProperty("日期")
    @ColumnWidth(10)
    @Schema(description = "日期")
    private String dayStr;

    /**
     * 币种
     */
    @Schema(description ="币种")
    @ExcelProperty("主币种")
    @ColumnWidth(10)
    private String currencyCode;


    /**
     * 存款人数
     */
    @Schema(description ="存款人数")
    @ExcelProperty("存款人数")
    @ColumnWidth(10)
    private Integer depositorsNums;

    /**
     * 存款次数
     */
    @Schema(description ="存款次数")
    @ExcelProperty("存款次数")
    @ColumnWidth(10)
    private Integer depositTimes;

    /**
     * 存款总金额
     */
    @Schema(description ="存款总额")
    @ExcelProperty("存款总额")
    @ColumnWidth(10)
    private BigDecimal depositTotalAmount;


    /**
     * 上级转入人数
     */
    @Schema(description ="上级转入人数")
    @ExcelProperty("上级转入人数")
    @ColumnWidth(10)
    private Integer depositSubordinatesNums;

    @Schema(description ="上级转入次数")
    @ExcelProperty("上级转入次数")
    @ColumnWidth(10)
    private Integer depositSubordinatesTimes;

    /**
     * 上级转入总额
     */
    @Schema(description ="上级转入总额")
    @ExcelProperty("上级转入总额")
    @ColumnWidth(10)
    private BigDecimal depositSubordinatesAmount;

    /**
     * 取款人数
     */
    @Schema(description ="取款人数")
    @ExcelProperty("取款人数")
    @ColumnWidth(10)
    private Integer withdrawalsNums;

    /**
     * 大额取款人数
     */
    @Schema(description ="大额取款人数")
    @ExcelProperty("大额取款人数")
    @ColumnWidth(10)
    private Integer bigMoneyWithdrawalsNums;

    /**
     * 大额取款次数
     */
    @Schema(description ="取款次数")
    @ExcelProperty("取款次数")
    @ColumnWidth(10)
    private Integer withdrawTimes;

    /**
     * 大额取款次数
     */
    @Schema(description ="大额取款次数")
    @ExcelProperty("大额取款次数")
    @ColumnWidth(10)
    private Integer bigMoneyWithdrawTimes;

    /**
     * 大额取款总金额
     */
    @Schema(description ="大额取款金额")
    @ExcelProperty("大额取款金额")
    @ColumnWidth(10)
    private BigDecimal bigMoneyWithdrawAmount;

    /**
     * 取款总金额
     */
    @Schema(description ="取款总额")
    @ExcelProperty("取款总额")
    @ColumnWidth(10)
    private BigDecimal withdrawTotalAmount;



    /**
     * 存取款差额
     */
    @Schema(description ="存取差")
    @ExcelProperty("存取差")
    @ColumnWidth(10)
    private BigDecimal depositWithdrawalDifference;

    public String getDayStr(){
        return day == null ? "" : DateUtils.formatDateByZoneId(day,DateUtils.DATE_FORMAT_1 ,CurrReqUtils.getTimezone());
    }

}
