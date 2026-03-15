package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @description 兑换码详情表,每个币种的兑换码都会保存一条记录
 * @author BEJSON.com
 * @date 2025-10-27
 */
@Builder
@Data
@AllArgsConstructor
public class SiteActivityRedemptionCodeDetailVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 兑换码详情id
     */
    private Long id;

    /**
     * 活动ID，关联兑换码主键id
     */
    @Schema(description = "活动ID，关联兑换码主键id")
    @NotNull(message = "活动ID不能为空")
    private Long activityId;

    /**
     * 兑换码类型，0:通用兑换码，1:唯一兑换码
     */
    @Schema(description = "兑换码类型，0:通用兑换码，1:唯一兑换码")
    @NotNull(message = "兑换码类型不能为空")
    @I18nField(type= I18nFieldTypeConstants.DICT)
    private Integer category;

    /**
     * 站点编码
     */
    @Schema(description = "站点编码")
    @NotNull(message = "站点编码不能为空")
    private String siteCode;

    /**
     * 兑换使用人数上限，兑换码类型=0，且top_limit=0时，表示为通用码，没有兑换人数限制；兑换码类型=1时，top_limit=1，1人1码
     */
    @Schema(description = "兑换使用人数上限" )
    private Integer topLimit;

    /**
     * 兑换条件，1:无限制用户，2:存款用户，3：当天存款用户（兑换码生效当天）；4：三天内存款用户
     */
    @Schema(description = "兑换条件")
    @I18nField(type= I18nFieldTypeConstants.DICT)
    private Integer condition;

    /**
     * 奖金
     */
    @Schema(description = "兑换条件")
    @NotNull(message = "兑换金额不能为空或0")
    private BigDecimal award;

    /**
     * 兑换码数量
     */
    @Schema(description = "兑换码数量")
    private Integer quantity;

    /**
     * 币种
     */
    @Schema(description = "币种")
    private String currency;

    /**
     * 订单号，活动兑换码主表中的order_no，作为冗余字段，不需要查主表
     */
    @Schema(description = "订单号")
    private String orderNo;

    /**
     * 创建时间
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

    /**
     * 流码倍率，可以针对每种币种设置不同的值
     */
    @Schema(description = "流码倍率")
    private BigDecimal washRatio;

    /**
     * 兑换码生效时间，精确到秒
     */
    @Schema(description = "兑换码生效时间")
    private Long startTime;

    /**
     * 兑换码失效时间
     */
    @Schema(description = "兑换码失效时间")
    private Long endTime;

}
