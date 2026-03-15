package com.cloud.baowang.agent.api.vo.commission;


import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/10/03 22:22
 * @description: agent_rebate_report_detail
 */
@Data
@Schema(title = "代理返点详情")
@I18nClass
public class RebateDetailVO {
    @Schema(title = "场馆类型")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.VENUE_TYPE)
    private String venueType;
    @Schema(title = "场馆类型名称")
    private String venueTypeText;
    @Schema(title = "返点比例")
    private BigDecimal rebateRate;

}
