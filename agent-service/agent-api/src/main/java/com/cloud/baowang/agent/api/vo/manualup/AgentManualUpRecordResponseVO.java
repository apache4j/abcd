package com.cloud.baowang.agent.api.vo.manualup;

import com.cloud.baowang.common.core.annotations.I18nClass;
import com.cloud.baowang.common.core.annotations.I18nField;
import com.cloud.baowang.common.core.constants.CommonConstant;
import com.cloud.baowang.common.core.constants.I18nFieldTypeConstants;
import com.cloud.baowang.common.core.utils.DateUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author: kimi
 */
@Data
@Schema(description = "代理人工加额记录 返回")
@I18nClass
public class AgentManualUpRecordResponseVO {

    @Schema(description = "代理id")
    private String agentId;

    @Schema(description = "订单号")
    private String orderNo;

    @Schema(description = "代理账号")
    private String agentAccount;

    @Schema(description = "钱包类型 1佣金钱包 2额度钱包")
    private Integer walletType;

    @Schema(description = "调整方式")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.MANUAL_ADJUST_WAY)
    private Integer adjustWay;

    @Schema(description = "调整方式")
    private String adjustWayText;

    @Schema(description = "订单状态")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.USER_REVIEW_REVIEW_STATUS)
    private Integer orderStatus;

    @Schema(description = "订单状态-Name")
    private String orderStatusText;

    @Schema(description = "调整类型")
    @I18nField(type = I18nFieldTypeConstants.DICT, value = CommonConstant.AGENT_MANUAL_ADJUST_TYPE)
    private Integer adjustType;

    @Schema(description = "调整类型-Name")
    private String adjustTypeText;

    @Schema(description = "币种")
    private String currencyCode;

    @Schema(description = "调整金额")
    private BigDecimal adjustAmount;

    @Schema(description = "申请时间")
    private Long applyTime;

    @Schema(description = "申请人")
    private String applicant;

    @Schema(description = "备注")
    private String applyReason;

    @Schema(description = "审核人")
    private String oneReviewer;

    @Schema(description = "审核时间")
    private Long oneReviewFinishTime;
    @Schema(description = "审核备注")
    private String oneReviewRemark;

    private Long updatedTime;

    /**
     * 存取款方式
     * DepositWithdrawalOrderTypeEnum
     */
    private Integer depositWithDrawType;

    @Schema(description = "调整次数")
    private Integer adjustTimes;

}
