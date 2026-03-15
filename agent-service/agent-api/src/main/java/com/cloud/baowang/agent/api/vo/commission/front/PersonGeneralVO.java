package com.cloud.baowang.agent.api.vo.commission.front;

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
 * @author: fangfei
 * @createTime: 2024/11/07 12:55
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
@Schema(title = "人头费", description = "人头费")
public class PersonGeneralVO {
    @Schema(description = "人头费")
    private BigDecimal commissionAmount = new BigDecimal("0.0000");

    @Schema(description = "结算周期  1 自然日 2 自然周  3 自然月")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SETTLE_CYCLE)
    private Integer settleCycle;

    @Schema(description = "结算周期  1 自然日 2 自然周  3 自然月")
    private String settleCycleText;

    @Schema(description = "计算开始时间")
    private Long startTime;

    @Schema(description = "计算结束时间")
    private Long endTime;
    @Schema(title = "有效新增人头费/人")
    private BigDecimal newUserAmount = new BigDecimal("0.0000");

    @Schema(description = "有效新增")
    private Integer newActiveNumber = 0;

    @Schema(description = "人头费调整金额-审核界面")
    private BigDecimal reviewAdjustAmount = new BigDecimal("0.0000");
}
