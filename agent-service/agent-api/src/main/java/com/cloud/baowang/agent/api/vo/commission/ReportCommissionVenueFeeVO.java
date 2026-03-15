package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/05 22:27
 * @description:
 */
@Data
@I18nClass
@Schema(title = "佣金场馆费率详情", description = "佣金场馆费率详情")
public class ReportCommissionVenueFeeVO {
    @Schema(description = "场馆code")
    @I18nField(type= I18nFieldTypeConstants.DICT,value = CommonConstant.VENUE_CODE)
    private String venueCode;

    @Schema(description = "场馆名称")
    private String venueCodeText;

    @Schema(description = "平台输赢 没减打赏金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal platWinLossAmount;

    @Schema(description = "有效流水")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal validAmount;

    @Schema(description = "负盈利费率")
    private String rate;

    @Schema(description = "有效流水费率")
    private String validRate;

    @Schema(description = "币种")
    private String currency;

    @Schema(description = "场馆费")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal venueFee;

    @Schema(description = "平台名称")
    private String venuePlatformName;

}
