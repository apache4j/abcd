package com.cloud.baowang.activity.api.vo;

import com.cloud.baowang.activity.api.vo.v2.FixedAmountV2VO;
import com.cloud.baowang.activity.api.vo.v2.RechargePercentageV2VO;
import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.ConstantsCode;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.vo.base.I18nMsgFrontVO;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @className: DepositConfigDTO
 * @author: wade
 * @description: 首存，次存，指定日存款
 * @date: 3/4/25 10:06
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@I18nClass
public class DepositConfigInsertDTO {
    /**
     * 场馆类型: 1:体育 2:视讯 3:棋牌 4:电子
     */
    @Schema(title = "游戏类型: 1:体育 2:视讯 3:棋牌 4:电子,场馆类型：字典CODE：venue_type")
    private String venueType;

    /**
     * 优惠方式类型，0.百分比，1.固定
     * {@link com.cloud.baowang.activity.api.enums.ActivityDiscountTypeEnum}
     */
    @Schema(description = "优惠方式类型，0.百分比，1.固定")
    @NotNull(message = "优惠方式类型不能为空")
    @Min(value = 0, message = "优惠方式类型不能小于0")
    @Max(value = 1, message = "优惠方式类型不能大于1")
    private Integer discountType;
    /**
     * 洗码倍率
     */
    @Schema(description = "洗码倍率")
    @NotNull(message = "洗码倍率不能为空")
    private BigDecimal washRatio;


    @Schema(description = "百分比类型对应条件值--优惠方式==0")
    private List<RechargePercentageVO> percentageVO;

    @Schema(description = "固定金额对应条件值--优惠方式==1")
    private List<FixedAmountVO> fixedAmountVOS;


    @Schema(description = "活动规则-多语言")
    @I18nField(type = I18nFieldTypeConstants.DICT_LIST)
    private String activityRuleI18nCode;

    /*@Schema(description = "活动规则-多语言")
    @NotNull(message = ConstantsCode.PARAM_ERROR)
    private List<I18nMsgFrontVO> activityRuleI18nCodeList;*/

}
