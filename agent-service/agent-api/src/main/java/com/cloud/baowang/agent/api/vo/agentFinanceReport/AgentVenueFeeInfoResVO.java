package com.cloud.baowang.agent.api.vo.agentFinanceReport;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "代理财务报表-平台费详情返参VO")
@I18nClass
public class AgentVenueFeeInfoResVO {
    @Schema(description = "代理id")
    private String agentId;
    @Schema(description = "代理账号")
    private String agentAccount;
    @Schema(description = "场馆费")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal feeAmount = BigDecimal.ZERO;
    @Schema(description = "负盈利费率")
    private BigDecimal venueRate = BigDecimal.ZERO;
    @Schema(description = "有效流水费率")
    private BigDecimal venueValidRate = BigDecimal.ZERO;
    @Schema(description = "场馆code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;
    @Schema(description = "场馆名称")
    private String venueCodeText;
    @Schema(description = "总输赢")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal totalWinLoseAmount = BigDecimal.ZERO;
    @Schema(description = "币种")
    private String currency;

    @Schema(description = "有效流水")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal venueValid;

    @Schema(description = "游戏平台名称")
    private String venuePlatformName;

}
