package com.cloud.baowang.user.api.vo.user.excel;

import cn.hutool.core.date.DatePattern;
import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

/**
 * @author: kimi
 */
@Data
@Schema(title = "审核列表 返回")
@I18nClass
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@ExcelIgnoreUnannotated
public class UserReviewExportVO {

    @Schema(title = "id")
    private String id;

    @Schema(title = "审核单号")
    @ExcelProperty("审核单号")
    @ColumnWidth(25)
    private String reviewOrderNo;

    @Schema(title = "申请信息")
    @ExcelProperty("申请信息")
    @ColumnWidth(25)
    private String applyInfo;

    @Schema(title = "申请时间")
    private Long applyTime;

    @Schema(title = "申请时间")
    @ExcelProperty("申请时间")
    @ColumnWidth(25)
    private String applyTimeStr;

    public String getApplyTimeStr() {
        return null == applyTime ? "" : TimeZoneUtils.formatTimestampToTimeZone(applyTime, CurrReqUtils.getTimezone());

    }

    @Schema(title = "申请人")
    @ExcelProperty("申请人")
    @ColumnWidth(25)
    private String applicant;

    @Schema(title = "一审完成时间")
    private Long oneReviewFinishTime;

    @Schema(title = "一审完成时间")
    @ExcelProperty("一审完成时间")
    @ColumnWidth(25)
    private String oneReviewFinishTimeStr;

    public String getOneReviewFinishTimeStr() {
        return null == oneReviewFinishTime ? "" : TimeZoneUtils.formatTimestampToTimeZone(oneReviewFinishTime, CurrReqUtils.getTimezone());
    }

    @Schema(title = "一审人")
    @ExcelProperty("一审人")
    @ColumnWidth(25)
    private String reviewer;

    @Schema(title = "审核操作 1一审审核 2结单查看")
    private Integer reviewOperation;

    @I18nField
    @Schema(title = "审核操作 1一审审核 2结单查看")
    @ExcelProperty("审核操作")
    @ColumnWidth(25)
    private String reviewOperationName;

    @Schema(title = "审核状态 1待处理 2处理中 3审核通过 4一审拒绝")
    private Integer reviewStatus;

    @I18nField
    @Schema(title = "审核状态 1待处理 2处理中 3审核通过 4一审拒绝")
    @ExcelProperty("审核状态")
    @ColumnWidth(25)
    private String reviewStatusName;

    @Schema(title = "锁单状态 0未锁 1已锁")
    @ExcelProperty("锁单状态 0未锁 1已锁")
    @ColumnWidth(25)
    private Integer lockStatus;

    @Schema(title = "锁单人")
    @ExcelProperty("锁单人")
    @ColumnWidth(25)
    private String locker;
    @ExcelProperty("锁单人是否当前登录人")
    @ColumnWidth(25)
    @Schema(title = "锁单人是否当前登录人 0否 1是")
    private Integer isLocker;

    @Schema(title = "申请人是否当前登录人 0否 1是")
    @ExcelProperty("申请人是否当前登录人 0否 1是")
    @ColumnWidth(25)
    private Integer isApplicant;
}
