package com.cloud.baowang.agent.api.vo.manualup;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
 * 代理审核详情
 *
 * @author kimi
 * @since 2023-05-02 10:00:00
 */
@Data
@Accessors(chain = true)
@Schema(description = "代理审核详情")
@I18nClass
public class AgentReviewDetailVO {

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "申请人")
    private String applicant;

    @Schema(description = "申请时间")
    private Long applyTime;

    @Schema(description = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_MANUAL_ADJUST_TYPE)
    private Integer adjustType;

    @Schema(description = "调整类型-Name")
    private String adjustTypeText;

    @Schema(description = "调整钱包")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_WALLET_TYPE)
    private Integer walletType;

    @Schema(description = "调整钱包")
    private String walletTypeText;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;

    @Schema(description = "申请原因")
    private String applyReason;

    @Schema(description = "上传附件地址")
    private String certificateAddress;
    @Schema(description = "上传附件地址-完整地址")
    private String certificateAddressAll;

}
