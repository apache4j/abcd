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
@Schema(description ="下级详情-游戏动态vo")
public class AgentLowerLevelInfoGameDynamicVO {
    @Schema(description ="订单号")
    private String orderId;
    @Schema(description = "场馆code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;
    @Schema(description ="场馆名称")
    private String venueCodeText;

    @Schema(title = "游戏平台名称")
    private String venuePlatformName;
    @Schema(description ="投注金额")
    private BigDecimal betAmount;
    @Schema(description ="游戏输赢")
    private BigDecimal winLoseAmount;
    @Schema(description ="投注时间")
    private Long betTime;
}
