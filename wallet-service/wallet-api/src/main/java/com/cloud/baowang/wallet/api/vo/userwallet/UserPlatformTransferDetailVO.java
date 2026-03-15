package com.cloud.baowang.wallet.api.vo.userwallet;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.AppBigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(title = "会员交易记录平台币兑换详情对象")
@I18nClass
public class UserPlatformTransferDetailVO {

    @Schema(description = "订单编码")
    private String orderNo;

    @Schema(description = "交易方式类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.TRADE_WAY_TYPE )
    private String tradeWayType;

    @Schema(description = "交易方式类型名称")
    private String tradeWayTypeText;


    @Schema(description ="到账金额")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal arriveAmount;

    @Schema(description = "平台币兑换金额")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal transferAmount;

    @Schema(description = "转换汇率")
    private BigDecimal transferRate;

    @Schema(description ="客户端状态 0处理中 1成功 2失败 ")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DEPOSIT_WITHDRAW_CUSTOMER_STATUS)
    private String customerStatus;

    @Schema(description ="客户端状态名称")
    private String customerStatusText;

    @Schema(description ="转换时间")
    private Long updatedTime;
}
