package com.cloud.baowang.system.api.vo.site.rebate;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@Schema(description = "不返水配置列表导出vo")
@ExcelIgnoreUnannotated
@NoArgsConstructor
@AllArgsConstructor
public class SiteNonRebateExportVO implements Serializable {

    @Schema(title = "场馆类型")
    private Integer venueType;

    @Schema(title = "场馆类型-文本")
    @ExcelProperty("场馆类型")
    private String venueValue;

    @Schema(title = "场馆code")
    private String venueCode;


    @Schema(title = "场馆code")
    @ExcelProperty("场馆名称")
    private String venueName;

    @Schema(description = "游戏信息")
    @ExcelProperty("不返水游戏名称")
    @ColumnWidth(50)
    private String gameInfo;

    @Schema(description = "操作人")
    @ExcelProperty("最近操作人")
    private String updater;

    @Schema(description = "最近操作时间")
    @ExcelProperty("最近操作时间")
    @DateTimeFormat("yyyy-MM-dd HH:mm:ss")
    private String updatedTime;

}
