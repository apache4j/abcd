package com.cloud.baowang.agent.api.vo.commission;

import com.cloud.baowang.agent.api.enums.commission.CommissionTypeEnum;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: fangfei
 * @createTime: 2024/10/28 14:55
 * @description: 佣金账单信息
 */
@Data
@I18nClass
@Schema(description = "人头费佣金账单信息")
public class PersonCommissionBillVO {
    @Schema(description = "期号")
    private String issue;

    /** {@link CommissionTypeEnum}*/
    /** 佣金类型 */
    @Schema(description = "佣金类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.COMMISSION_TYPE)
    private String commissionType;

    @Schema(description = "佣金类型名称")
    private String commissionTypeText;

    /**结算周期  1 自然日 2 自然周  3 自然月*/
    @Schema(description = "结算周期")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.SETTLE_CYCLE)
    private Integer settleCycle;

    @Schema(description = "结算周期")
    private String settleCycleText;

    @Schema(description = "结算开始时间")
    private Long startTime;

    @Schema(description = "结算结束时间")
    private Long endTime;

    @Schema(description = "结算币种")
    private String currency;

    @Schema(description = "有效新增人头费/人")
    private BigDecimal newUserAmount;

    @Schema(description = "有效新增会员数")
    private Integer newActiveNumber;

    @Schema(description = "有效新增人头费-申请")
    private BigDecimal applyAmount;

    @Schema(description = "调整有效新增人头费")
    private BigDecimal adjustCommissionAmount;

    @Schema(description = "实际发放有效新增人头费")

    private BigDecimal commissionAmount;

    @Schema(description = "佣金调整备注")
    private String adjustCommissionRemark;
}
