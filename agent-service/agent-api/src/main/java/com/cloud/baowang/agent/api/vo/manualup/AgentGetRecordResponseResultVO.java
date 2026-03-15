package com.cloud.baowang.agent.api.vo.manualup;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author: kimi
 */
@Data
@Schema(title = "代理加额审核记录-列表 返回")
@I18nClass
public class AgentGetRecordResponseResultVO {

    @Schema(title = "id")
    private String id;

    @Schema(title = "订单号")
    private String orderNo;

    @Schema(title = "代理账号")
    private String agentAccount;

    @Schema(title = "代理姓名")
    private String agentName;

    @Schema(title = "审核状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer orderStatus;

    @Schema(title = "订单状态-Name")
    private String orderStatusText;

    @Schema(description = "调整钱包")
    @I18nField(type = I18nFieldTypeConstants.DICT,value = CommonConstant.AGENT_WALLET_TYPE)
    private Integer walletType;

    @Schema(description = "调整钱包")
    private String walletTypeText;

    @Schema(title = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_MANUAL_ADJUST_TYPE)
    private Integer adjustType;

    @Schema(title = "调整类型-Name")
    private String adjustTypeText;

    @Schema(title = "币种")
    private String currencyCode;

    @Schema(title = "调整金额")
    private BigDecimal adjustAmount;


    @Schema(title = "申请时间")
    private Long applyTime;

    @Schema(title = "一审人")
    private String oneReviewer;

    @Schema(title = "一审完成时间")
    private Long oneReviewFinishTime;

    @Schema(title = "一审审核用时")
    private String oneReviewUseTime;

    @Schema(title = "一审备注")
    private String oneReviewRemark;
}
