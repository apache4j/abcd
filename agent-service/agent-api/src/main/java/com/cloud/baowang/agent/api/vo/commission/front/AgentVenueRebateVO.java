package com.cloud.baowang.agent.api.vo.commission.front;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.BigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/11/09 23:32
 * @description: 场馆返点明细
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "场馆返点明细", description = "场馆返点明细")
public class AgentVenueRebateVO {
    @Schema(title = "场馆类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_TYPE)
    private Integer venueType;
    @Schema(title = "场馆类型名称")
    private String venueTypeText;
    @Schema(title = "币种")
    private String currency;
    @Schema(description = "有效流水")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal validAmount;
    @Schema(title = "返点比例")
    private String rebateRate;
    @Schema(description = "返点金额")
    @JsonSerialize(using = BigDecimalJsonSerializer.class)
    private BigDecimal rebateAmount;
}
