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
@Schema(description = "代理加额审核记录-列表 返回")
@I18nClass
public class AgentGetRecordResponseVO {

    @Schema(description = "id")
    private String id;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "代理姓名")
    private String agentName;

    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer orderStatus;

    @Schema(description = "订单状态-Name")
    private String orderStatusText;

    @Schema(description = "钱包类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_WALLET_TYPE)
    private Integer walletType;

    @Schema(description = "钱包类型")
    private String walletTypeText;

    @Schema(description = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_MANUAL_ADJUST_TYPE)
    private Integer adjustType;

    @Schema(description = "调整类型")
    private String adjustTypeText;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;

    @Schema(description = "申请时间")
    private Long applyTime;

    @Schema(description = "一审人")
    private String oneReviewer;

    @Schema(description = "二审人")
    private String twoReviewer;

    @Schema(description = "一审完成时间")
    private Long oneReviewFinishTime;

    @Schema(description = "一审开始时间")
    private Long oneReviewStartTime;

    @Schema(description = "一审审核用时")
    private String oneReviewUseTime;

    @Schema(description = "一审备注")
    private String oneReviewRemark;
}
