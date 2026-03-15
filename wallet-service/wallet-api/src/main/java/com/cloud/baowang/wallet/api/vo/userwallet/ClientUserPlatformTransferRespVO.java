package com.cloud.baowang.wallet.api.vo.userwallet;

import com.alibaba.excel.annotation.ExcelIgnoreUnannotated;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.cloud.baowang.common.core.serializer.AppBigDecimalJsonSerializer;
import com.cloud.baowang.common.core.utils.CurrReqUtils;
import com.cloud.baowang.common.core.utils.DateUtils;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author qiqi
 **/
@Data
@Schema(title = "平台币兑换记录返回")
public class ClientUserPlatformTransferRespVO {


    /**
     * 订单号
     */
    @Schema(description = "订单号")
    private String orderNo;
    /**
     * 订单时间
     */
    @Schema(description = "转换时间")
    private Long orderTime;

    /**
     * 会员ID
     */
    private String userId;

    /**
     * 会员ID
     */
    @Schema(description = "会员账号")
    private String userAccount ;

    /**
     * 平台币币种
     */
    @Schema(description = "平台币币种")
    private String platCurrencyCode;

    /**
     * 转换金额
     */
    @Schema(description = "平台币金额")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal transferAmount;

    /**
     * 转换币种 用户法币
     */
    @Schema(description = "转换币种")
    private String targetCurrencyCode;

    /**
     * 汇率
     */
    @Schema(description = "汇率")
    private BigDecimal transferRate;
    /**
     * 目标金额
     */
    @Schema(description = "兑换金额")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal targetAmount;
}
