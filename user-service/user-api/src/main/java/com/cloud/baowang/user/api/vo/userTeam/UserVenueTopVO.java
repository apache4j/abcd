package com.cloud.baowang.user.api.vo.userTeam;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @author qiqi
 */
@Data
@I18nClass
@AllArgsConstructor
@NoArgsConstructor
@Schema(title = "用户TOP平台统计")
public class UserVenueTopVO {

    @Schema(description = "场馆code")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.VENUE_CODE)
    private String venueCode;

    @Schema(description = "原始投注币种")
    private String currency;

    @Schema(description = "场馆名称")
    private String venueCodeText;

    @Schema(description ="会员输赢 平台币")
    private BigDecimal winLossAmount;

    @Schema(description ="投注金额 平台币")
    private BigDecimal betAmount;

    @Schema(description ="有效投注 平台币")
    private BigDecimal validAmount;
}
