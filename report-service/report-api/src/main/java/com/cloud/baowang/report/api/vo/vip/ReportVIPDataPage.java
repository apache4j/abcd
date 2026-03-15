package com.cloud.baowang.report.api.vo.vip;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 2024/11/6 19:41
 * @Version : 1.0
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Schema(description = "vip报表查询返回分页数据")
@I18nClass
@ExcelIgnoreUnannotated
public class ReportVIPDataPage implements Serializable {

    @Schema(title = "日期显示")
    private Long dateShow;

    @Schema(description = "日期字符串")
    @ExcelProperty("日期字符串")
    @ColumnWidth(20)
    private String dateShowStr;

    @Schema(title = "VIP段位code")
    private Integer vipRankCode;

    @Schema(title = "VIP段位名称")
    @I18nField
    @ExcelProperty("VIP段位")
    @ColumnWidth(20)
    private String vipRankCodeName;

    @Schema(title = "VIP等级code")
    private Integer vipGradeCode;

    @Schema(title = "VIP等级名称")
    @I18nField
    @ExcelProperty("VIP等级")
    @ColumnWidth(20)
    private String vipGradeCodeName;

    @Schema(title = "当前VIP等级现有人数")
    @ExcelProperty("现有人数")
    @ColumnWidth(20)
    private Integer currentGradeNum;

    @Schema(title = "达成等级的人数")
    @ExcelProperty("新达成人数")
    @ColumnWidth(20)
    private Integer achieveGradeNum;

    @Schema(title = "已领取的红利")
    @ExcelProperty("已领取红利")
    @ColumnWidth(20)
    private BigDecimal receiveBonus;

    public String getDateShowStr() {
        return null == dateShow ? "" : TimeZoneUtils.formatTimestampToTimeZone(dateShow, CurrReqUtils.getTimezone());
    }
}
