package com.cloud.baowang.wallet.api.vo.userDividend;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author : qiqi
 */
@Schema(title ="用户红利返回对象")
@Data
@I18nClass
public class UserDividendPageVO implements Serializable {

    @Schema(description="会员账号")
    private String userAccount;

    @Schema(description="币种")
    private String currencyCode;


    @Schema(description="红利类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DIVIDEND_TYPE)
    private Integer dividendType;

    @Schema(description="红利类型名称")
    private String dividendTypeText;

    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    @Schema(description="红利金额")
    private BigDecimal dividendAmount;

    @Schema(description="发放时间")
    private Long applyTime;

    @Schema(description="领取奖励时间")
    private Long receiveTime;

    @Schema(description="红利状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.DIVIDEND_STATUS)
    private Integer dividendStatus;

    @Schema(description="红利状态名称")
    private String dividendStatusText;

}
