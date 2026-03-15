package com.cloud.baowang.system.api.vo.site.rebate.user;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ExcelIgnoreUnannotated
@Schema(description = "游戏报表站点返回vo")
@I18nClass
public class UserRebateRecordExportVO implements Serializable {

    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    private String orderNo;

    @Schema(description = "审核状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COMMISSION_REVIEW_STATUS)
    private Integer orderStatus;


    @Schema(description = "审核状态名称")
    @ExcelProperty("订单状态")
    private String orderStatusText;


    @Schema(description = "用户账号")
    @ExcelProperty("会员账号")
    private String userAccount;

    @Schema(description = "会员段位")
    @I18nField
    @ExcelProperty("会员段位")
    private String vipRankName;


    @Schema(description = "统计日期 生成数据日期减1")
    @ExcelProperty("统计日期")
    private String statisticsDateStr;

    @Schema(description = "币种")
    @ExcelProperty("币种")
    private String currencyCode;

    @Schema(description = "有效投注")
    @ExcelProperty("有效投注")
    private BigDecimal validAmount;

    @Schema(description = "返水金额")
    @ExcelProperty("返水金额")
    private BigDecimal rebateAmount;



    @Schema(description = "申请时间")
    private Long createdTime;
    @ExcelProperty("申请时间")
    private String createdTimeStr;

    @Schema(description = "审核员")
    @ExcelProperty("审核员")
    private String auditAccount;

    @Schema(description = "审核时间")
    private Long auditTime;

    @ExcelProperty("审核时间")
    private String auditTimeStr;


    @Schema(description = "审核用时-秒")
    @ExcelProperty("审核用时")
    private Long auditTimeSec;

    public String getCreatedTimeStr() {
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }

    public String getAuditTimeStr() {
        return auditTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(auditTime, CurrReqUtils.getTimezone());
    }

}
