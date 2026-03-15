package com.cloud.baowang.admin.vo.export;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通道发送统计表
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ExcelIgnoreUnannotated
public class ChannelSendingStatisticExportVO {


    @ExcelProperty("通道名称")
    @ColumnWidth(20)
    private String channelName;

    @ExcelProperty("通道代码")
    @ColumnWidth(20)
    private String channelCode;



    @ExcelProperty("通道ID")
    @ColumnWidth(20)
    private String channelId;


    @ExcelProperty("发送总量")
    @ColumnWidth(20)
    private Long sendCount;

}
