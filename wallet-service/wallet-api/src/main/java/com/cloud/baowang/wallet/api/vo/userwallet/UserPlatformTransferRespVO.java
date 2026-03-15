package com.cloud.baowang.wallet.api.vo.userwallet;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Desciption:
 * @Author: Ford
 * @Date: 2024/10/16 10:01
 * @Version: V1.0
 **/
@Data
@Schema(title = "平台币兑换查询参数")
@ExcelIgnoreUnannotated
public class UserPlatformTransferRespVO  {
    /**
     * 站点code
     */
    private String siteCode;

    /**
     * 订单号
     */
    @Schema(description = "订单号")
    @ExcelProperty(value = "订单号")
    @ColumnWidth(32)
    private String orderNo;
    /**
     * 订单时间
     */
    @Schema(description = "兑换时间")
    private Long orderTime;



    /**
     * 会员ID
     */
    private String userId;

    /**
     * 会员ID
     */
    @Schema(description = "会员账号")
    @ExcelProperty(value = "会员账号")
    @ColumnWidth(32)
    private String userAccount ;

    /**
     * 平台币币种
     */
    @Schema(description = "平台币币种")
   // @ExcelProperty(value = "平台币币种")
   // @ColumnWidth(16)
    private String platCurrencyCode;

    /**
     * 兑换金额
     */
    @Schema(description = "兑换金额")
    @ExcelProperty(value = "兑换金额")
    @ColumnWidth(32)
    private BigDecimal transferAmount;

    /**
     * 兑换币种 用户法币
     */
    @Schema(description = "兑换币种")
    @ExcelProperty(value = "兑换币种")
    @ColumnWidth(16)
    private String targetCurrencyCode;

    /**
     * 汇率
     */
    @Schema(description = "汇率")
    @ExcelProperty(value = "汇率")
    @ColumnWidth(32)
    private BigDecimal transferRate;
    /**
     * 目标金额
     */
    @Schema(description = "主货币兑换金额")
    @ExcelProperty(value = "主货币兑换金额")
    @ColumnWidth(32)
    private BigDecimal targetAmount;

    @ExcelProperty(value = "兑换时间")
    @ColumnWidth(32)
    private String orderTimeStr;

    public String getOrderTimeStr() {
        return DateUtils.formatDateByZoneId(this.getOrderTime(), CurrReqUtils.getTimezone());
    }
}
