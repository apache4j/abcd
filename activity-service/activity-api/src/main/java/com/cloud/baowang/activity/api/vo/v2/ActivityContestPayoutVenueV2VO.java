package com.cloud.baowang.activity.api.vo.v2;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 *
 * @author brence
 * @description: 活动大类配置
 * @date:  2025-10-18
 */
@Schema(description = "活动大类配置")
@Data
@Slf4j
@I18nClass
public class ActivityContestPayoutVenueV2VO {

    /**
     * 游戏大类
     * system_param "venue_type"
     * {@link com.cloud.baowang.play.api.enums.venue.VenueTypeEnum }
     */
    @Schema(description = "活动大类,默认大类=体育")
    private String venueType;

    @Schema(description = "0:平台币, 1: 法币")
    private String platformOrFiatCurrency;

    @Schema(description = "活动规则-多语言")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityRuleI18nCode;

    @Schema(description = "活动规则-多语言")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> activityRuleI18nCodeList;

}
