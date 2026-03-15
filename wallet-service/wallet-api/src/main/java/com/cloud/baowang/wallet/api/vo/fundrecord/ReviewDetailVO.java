package com.cloud.baowang.wallet.api.vo.fundrecord;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 审核详情
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Accessors(chain = true)
@Schema(description = "审核详情")
@I18nClass
public class ReviewDetailVO {

    @Schema(description = "申请人")
    private String applicant;

    @Schema(description = "申请时间")
    private Long applyTime;

    @Schema(description = "申请原因")
    private String applyReason;

    @Schema(description = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.MANUAL_ADJUST_TYPE)
    private Integer adjustType;
    @Schema(description = "调整类型 - Text")
    private String adjustTypeText;

    @Schema(description = "活动类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.ACTIVITY_TEMPLATE)
    private String activityTemplate;

    @Schema(description = "活动类型 - Text")
    private String activityTemplateText;

    @Schema(description = "活动ID")
    private String activityId;

    @Schema(description = "活动名称")
    @I18nField
    private String activityNameI18nCode;

    @Schema(description = "流水倍数")
    private BigDecimal runningWaterMultiple;

    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;



    @Schema(description = "上传附件地址")
    private String certificateAddress;
    @Schema(description = "上传附件地址-完整地址")
    private String certificateAddressAll;
}
