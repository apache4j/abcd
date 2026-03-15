package com.cloud.baowang.system.api.vo.site.rebate.user;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.serializer.AppBigDecimalJsonSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;


@Data
@Schema(description = "返水明细vo")
@I18nClass
public class UserRebateDetailsRspVO implements Serializable {

    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子,场馆类型：字典CODE：venue_type")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.VENUE_TYPE)
    private String venueType;

    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子")
    private String venueTypeText;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "有效投注")
    private BigDecimal validAmount;

    @Schema(description = "返水比例")
    @JsonSerialize(using = AppBigDecimalJsonSerializer.class)
    private BigDecimal rebatePercent;

    @Schema(description = "返水金额")
    private BigDecimal rebateAmount;




}
