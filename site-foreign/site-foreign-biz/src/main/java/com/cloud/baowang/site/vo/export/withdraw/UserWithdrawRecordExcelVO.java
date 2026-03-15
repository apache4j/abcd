package com.cloud.baowang.site.vo.export.withdraw;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "会员提款重复项查询")
@ExcelIgnoreUnannotated
@I18nClass
public class UserWithdrawRecordExcelVO {

    @Schema(description = "会员账号")
    @ExcelProperty("会员账号")
    @ColumnWidth(25)
    private String userAccount;

    @Schema(description = "代理")
    @ExcelProperty("所属代理")
    @ColumnWidth(25)
    private String agentAccount;

    @Schema(description = "会员标签")
    @ExcelProperty("会员标签")
    @ColumnWidth(25)
    private String userLabelName;

    @Schema(description = "手机区号")
    @ExcelProperty("区号")
    private String areaCode;

    @ExcelProperty("手机号码")
    private String telephone;

    @ExcelProperty("邮箱")
    private String email;

    @Schema(description = "提款类型")
    private String depositWithdrawTypeId;

    @Schema(description = "提款类型Code")
    private String depositWithdrawTypeCode;


    @Schema(description = "提款类型")
    @ExcelProperty("提款类型")
    @ColumnWidth(25)
    private String depositWithdrawTypeName;

    @Schema(description = "提款方式")
    @ExcelProperty("提款方式")
    @ColumnWidth(25)
    @I18nField
    private String depositWithdrawWay;

    @Schema(description = "提款信息")
    @ExcelProperty("提款信息")
    @ColumnWidth(25)
    private String withdrawInfo;

    @Schema(description = "提款账号信息")
    @ExcelProperty("提款账号信息")
    @ColumnWidth(50)
    private String withdrawAccountInfoExcel;

    @Schema(description = "提款币种")
    @ExcelProperty("提款币种")
    @ColumnWidth(25)
    private String currencyCode;

    @Schema(description = "提款金额")
    @ExcelProperty("提款金额")
    @ColumnWidth(25)
    private BigDecimal applyAmount;


    @ExcelProperty("提款成功时间")
    @ColumnWidth(25)
    private String applyCompleteTimeStr;

    @Schema(description = "提款完成时间")
    private Long applyCompleteTime;

    public String getApplyCompleteTimeStr() {
        return applyCompleteTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(applyCompleteTime, CurrReqUtils.getTimezone());
    }


}
