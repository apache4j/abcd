package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description 兑换码兑换表
 * @author brence
 * @date 2025-10-27
 */
@Data
@Builder
@AllArgsConstructor
public class SiteActivityRedemptionCodeExchangeVO implements Serializable {

    /**
     * 兑换码领取记录id
     */
    private Long id;

    /**
     * 兑换码
     */
    @Schema(description = "兑换码")
    @NotNull(message = "兑换码不能为空")
    private String code;

    /**
     * 兑换码类型，0:通用兑换码，1:唯一兑换码
     */
    @Schema(description = "兑换码类型，0:通用兑换码，1:唯一兑换码")
    @I18nField(type = I18nFieldTypeConstants.DICT,value= CommonConstant.ENABLE_DISABLE_STATUS)
    private Integer category;

    @Schema(description = "订单号")
    private String orderNo;
    /**
     * 兑换码币种
     */
    @Schema(description = "兑换码币种")
    private String currency;

    /**
     * 兑换金额
     */
    @Schema(description = "兑换金额")
    @NotNull(message = "兑换金额不能为空")
    private BigDecimal amount;

    /**
     * 兑换码批次号
     */
    @Schema(description = "兑换码批次号")
    private String batchNo;

    /**
     * 兑换会员ID
     */
    @Schema(description = "兑换会员ID")
    private String userId;

    /**
     * 创建时间(兑换时间)
     */
    @Schema(description = "创建时间")
    private Long createdTime;

    /**
     * 更新时间
     */
    @Schema(description = "更新时间")
    private Long updatedTime;

    /**
     * 创建人
     */
    @Schema(description = "创建人")
    private String creator;

    /**
     * 修改人
     */
    @Schema(description = "修改人")
    private String updater;
}
