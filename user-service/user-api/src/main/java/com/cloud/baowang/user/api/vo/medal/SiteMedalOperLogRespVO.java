package com.cloud.baowang.user.api.vo.medal;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/8/20 10:59
 * @Version: V1.0
 **/
@Data
@Schema(description = "勋章变更记录 响应结果")
@I18nClass
@ExcelIgnoreUnannotated
public class SiteMedalOperLogRespVO {




    /**
     * 站点代码 -1代表总站默认值
     */
    @Schema(description = "站点编号")
    private String siteCode;

    /**
     * 站点勋章主键
     */
    @Schema(description = "站点勋章主键")
    private String siteMedalId;

    /**
     * 勋章代码
     */
    @Schema(description = "勋章代码")
    private String medalCode;

    /**
     * 勋章名称
     */
    @Schema(description = "勋章名称")
    @ExcelProperty(value="勋章名称")
    @ColumnWidth(30)
    private String medalName;

    /**
     * 操作时间
     */
    @Schema(description = "操作时间")
    private Long operTime;

    @Schema(description = "操作时间")
    @ExcelProperty(value="操作时间")
    @ColumnWidth(30)
    private String operTimeStr;


    public String getOperTimeStr() {
        return TimeZoneUtils.formatTimestampToTimeZone(this.getOperTime(), CurrReqUtils.getTimezone());
    }

    /**
     * 操作项
     */
    @Schema(description = "操作项")
    private String operItem;

    /**
     * 操作项多语言
     */
    @Schema(description = "操作项多语言")
    @I18nField
    @ExcelProperty(value="操作项")
    @ColumnWidth(20)
    private String operItemI18;

    /**
     * 变更前信息
     */
    @Schema(description = "变更前信息")
    @ExcelProperty(value="变更前信息")
    @ColumnWidth(45)
    private String operBefore;

    /**
     * 变更后信息
     */
    @Schema(description = "变更后信息")
    @ExcelProperty(value="变更后信息")
    @ColumnWidth(45)
    private String operAfter;


    @Schema(description = "创建人")
    @ExcelProperty(value="操作人")
    @ColumnWidth(20)
    private String creator;

    @Schema(description = "创建时间")
    private String createdTime;


    @Schema(description = "主键ID")
    @ExcelProperty(value="序列号")
    @ColumnWidth(40)
    private String id;

}
