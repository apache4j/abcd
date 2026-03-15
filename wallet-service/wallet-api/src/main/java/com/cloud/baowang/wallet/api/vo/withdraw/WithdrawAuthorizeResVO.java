package com.cloud.baowang.wallet.api.vo.withdraw;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @Author : 小智
 * @Date : 2024/7/30 11:46
 * @Version : 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "提款授权返回参数")
@I18nClass
public class WithdrawAuthorizeResVO {

    /**
     * 主键ID
     */
    @Schema(description = "主键ID")
    private String withdrawWayId;

    /**
     * 货币代码
     */
    @Schema(description = "货币代码")
    private String currencyCode;

    @Schema(description = "提款类型code")
    private String withdrawType;

    @Schema(description = "提款类型名称")
    @I18nField
    private String withdrawTypeName;

    @Schema(description = "提款方式")
    @I18nField
    private String withdrawWayName;

    @Schema(description = "手续费")
    private String withdrawFee;

    @Schema(description = "图标")
    private String wayIcon;

    @Schema(description = "备注")
    private String memo;

    @Schema(description = "状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ENABLE_DISABLE_STATUS)
    private String status;

    @Schema(description = "状态国际化")
    private String statusText;

    @Schema(description = "选中状态(0:未选中,1:选中)")
    private Integer chooseFlag;

    @Schema(description = "操作时间")
    private Long operatorTime;

    @Schema(description = "操作人")
    private String operator;

    @Schema(description = "手续费类型")
    private Integer feeType;

    @Schema(description = "手续费-固定金额")
    private BigDecimal wayFeeFixedAmount;
}
