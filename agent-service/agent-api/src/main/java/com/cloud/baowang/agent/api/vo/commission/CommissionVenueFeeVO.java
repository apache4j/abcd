package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.enums.ResultCode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: fangfei
 * @createTime: 2024/11/05 22:27
 * @description:
 */
@Data
@I18nClass
@Schema(title = "代理佣金场馆费率", description = "代理佣金场馆费率")
public class CommissionVenueFeeVO {
    @Schema(description = "场馆code")
    @I18nField(type= I18nFieldTypeConstants.DICT,value = CommonConstant.VENUE_CODE)
    private String venueCode;
    @Schema(description = "场馆名称")
    private String venueCodeText;
    @Schema(description = "负盈利费率")
    @NotNull(message = "负盈利费率不能为空")
    @DecimalMin(value ="0.00",message = "负盈利费率最小为0")
    private String rate;

    @Schema(title = "有效流水费率",description = "有效流水费率")
    @NotNull(message = "有效流水费率不能为空")
    @DecimalMin(value ="0.00",message = "有效流水费率最小为0")
    private String validRate;

    @Schema(description = "平台名称")
    private String venuePlatformName;

}
