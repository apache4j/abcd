package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@I18nClass
public class AgentCommissionReportVO implements Serializable {

    @Schema(description ="siteCode")
    private String siteCode;
    @Schema(description ="代理ID")
    private String agentId;

    @Schema(title = "场馆类型")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.VENUE_TYPE)
    private String venueType;
    @Schema(title = "场馆类型名称")
    private String venueTypeText;

    @Schema(description ="有效流水")
    private BigDecimal validAmount;

    @Schema(description ="有效流水")
    private BigDecimal commissionAmount;

    @Schema(description ="返点比例")
    private BigDecimal planRate;

    @Schema(description ="返点极差")
    private BigDecimal diffRate;
}
