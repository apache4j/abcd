package com.cloud.baowang.agent.api.vo.withdraw;

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
 * 本次提款详情
 *
 * @author qiqi
 */
@Data
@I18nClass
@ExcelIgnoreUnannotated
@Schema(title = "代理人工提款导出对象")
public class AgentWithdrawManualRecordExportVO {


    @Schema(description = "订单号")
    @ExcelProperty("订单号")
    @ColumnWidth(25)
    private String orderNo;

    @Schema(description = "代理账号")
    @ExcelProperty("代理账号")
    @ColumnWidth(25)
    private String agentAccount;

    @Schema(description = "提款方式")
    @I18nField
    @ExcelProperty("提款方式")
    @ColumnWidth(25)
    private String depositWithdrawWay;

    @Schema(description = "币种")
    @ExcelProperty("币种")
    @ColumnWidth(25)
    private String currencyCode;


    @Schema(description = "提款金额")
    @ExcelProperty("提款金额")
    @ColumnWidth(25)
    private BigDecimal applyAmount;


    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_CUSTOMER_STATUS)
    private String customerStatus;

    @Schema(description = "订单状态名称")
    @ExcelProperty("订单状态")
    @ColumnWidth(25)
    private String customerStatusText;

    @Schema(description = "出款人")
    @ExcelProperty("出款人")
    @ColumnWidth(25)
    private String updater;

    @Schema(description = "出款时间")
    private Long updatedTime;



    @Schema(description="出款时间;导出需要")
    @ExcelProperty("出款时间")
    @ColumnWidth(25)
    private String updatedTimeStr;


    public String getUpdatedTimeStr(){
        return updatedTime == null ? "" : TimeZoneUtils.formatTimestampToTimeZone(updatedTime, CurrReqUtils.getTimezone());
    }





}
