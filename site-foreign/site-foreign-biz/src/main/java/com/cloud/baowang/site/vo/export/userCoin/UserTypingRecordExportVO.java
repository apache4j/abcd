package com.cloud.baowang.site.vo.export.userCoin;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Schema(title = "会员流水变更记录导出对象")
@Data
@I18nClass
@ExcelIgnoreUnannotated
public class UserTypingRecordExportVO {


    /**
     * 关联订单号
     */
    @Schema(description="关联订单号")
    @ExcelProperty("关联订单号")
    @ColumnWidth(20)
    private String orderNo;

    @Schema(description="流水变动时间")
    private Long createdTime;

    @Schema(description="流水变动时间;导出需要")
    @ExcelProperty("流水变动时间")
    @ColumnWidth(20)
    private String createdTimeStr;

    @Schema(description="会员账号")
    @ExcelProperty("会员账号")
    @ColumnWidth(20)
    private String userAccount;

    @Schema(description="账号类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_ACCOUNT_TYPE )
    private String accountType;

    @Schema(description="账号类型")
    @ExcelProperty("账号类型")
    @ColumnWidth(20)
    private String accountTypeText;

    @Schema(description="流水类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.TYPING_ADJUST_TYPE)
    private String adjustType;

    @Schema(description="流水类型名称")
    @ExcelProperty("流水类型")
    @ColumnWidth(20)
    private String adjustTypeText;

    @Schema(description = "主货币")
    @ExcelProperty("主货币")
    @ColumnWidth(20)
    private String currency;

    /**
     * 变动前流水金额
     */
    @Schema(description="变动前流水金额")
    @ExcelProperty("变动前流水金额")
    @ColumnWidth(20)
    private BigDecimal coinFrom;


    @Schema(description="增减类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.TYPING_BALANCE_TYPE)
    private String adjustWay;

    @Schema(description="增减类型名称")
    @ExcelProperty("增减类型")
    @ColumnWidth(20)
    private String adjustWayText;




    /**
     * 变更金额
     */
    @Schema(description="变更金额")
    @ExcelProperty("变更金额")
    @ColumnWidth(20)
    private BigDecimal coinValue;

    /**
     * 变更后流水金额
     */
    @Schema(description="变更后流水金额")
    @ExcelProperty("变更后流水金额")
    @ColumnWidth(20)
    private BigDecimal coinTo;





    /**
     * 备注
     */
    @Schema(description="备注")
    private String remark;


  /*  public String getAccountStatusStr(){
        if(!CollectionUtils.isEmpty(this.accountStatusName)) {
            Set<String> set = this.accountStatusName.stream().map(CodeValueVO::getValue).collect(Collectors.toSet());
            return StringUtils.join(set, ",");
        }
        return StringUtils.EMPTY;
    }*/

    public String getCreatedTimeStr(){
        return createdTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(createdTime, CurrReqUtils.getTimezone());
    }
}
