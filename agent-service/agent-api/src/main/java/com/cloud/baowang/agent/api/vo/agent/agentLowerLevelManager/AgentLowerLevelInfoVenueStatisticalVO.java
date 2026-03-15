package com.cloud.baowang.agent.api.vo.agent.agentLowerLevelManager;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@I18nClass
public class AgentLowerLevelInfoVenueStatisticalVO {
    @Schema(description = "场馆code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;
    @Schema(description ="场馆名称")
    private String venueCodeText;
    @Schema(title = "游戏平台名称")
    private String venuePlatformName;

    @Schema(description ="投注金额")
    private BigDecimal betAmount = BigDecimal.ZERO;
    @Schema(description ="游戏输赢")
    private BigDecimal winLossAmount = BigDecimal.ZERO;
    @Schema(description ="币种")
    private String currency;



}
