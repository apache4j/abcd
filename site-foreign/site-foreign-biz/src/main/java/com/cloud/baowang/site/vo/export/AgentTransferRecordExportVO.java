package com.cloud.baowang.site.vo.export;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.TimeZoneUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@I18nClass
@ExcelIgnoreUnannotated
@Schema(title = "代理转账记录ExportVO")
public class AgentTransferRecordExportVO {
    @Schema(description ="订单号")
    @ExcelProperty("订单号")
    private String orderNo;

    @Schema(description ="转出代理账号")
    @ExcelProperty("转出代理账号")
    private String fromTransAccount;

    @Schema(description ="转出代理层级")
    @ExcelProperty("转出代理层级")
    private Integer fromTransLevel;

    @Schema(description ="转入代理账号")
    @ExcelProperty("转入代理账号")
    private String toTransAccount;

    @Schema(description ="转出钱包code")
    private String agentWalletType;

    @Schema(description ="转出钱包名称")
    @ExcelProperty("转出钱包")
    private String agentWalletTypeName;

    @Schema(description ="订单状态")
    private Integer orderStatus;

    @Schema(description ="订单状态名称")
    @ExcelProperty("订单状态")
    private String orderStatusName;

    @Schema(description ="转账金额")
    @ExcelProperty("转账金额")
    private BigDecimal transferAmount;

    @Schema(description ="转账时间")
    private Long transferTime;


    @Schema(description ="转账时间")
    @ExcelProperty("转账时间")
    private String transferTimeStr;

    @Schema(description ="备注")
    @ExcelProperty("备注")
    private String remark;

    public String getTransferTimeStr() {
        return transferTime == null ? null : TimeZoneUtils.formatTimestampToTimeZone(transferTime, CurrReqUtils.getTimezone());
    }


}
